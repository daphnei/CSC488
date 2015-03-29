package compiler488.codegen;

import compiler488.ast.Printable;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.AnonFuncExpn;
import compiler488.ast.expn.ArithExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.Stmt;
import compiler488.compiler.Main;
import compiler488.exceptions.ExecutionException;
import compiler488.exceptions.MemoryAddressException;
import compiler488.runtime.Machine;
import compiler488.semantics.NodeVisitor;
import compiler488.semantics.types.ArraySemType;
import compiler488.semantics.types.PrimitiveSemType;
import compiler488.semantics.types.RoutineSemType;
import compiler488.symbol.CodeGenSymbolTable;
import compiler488.symbol.ScopeType;
import compiler488.symbol.SymScope;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

public class CodeGenVisitor extends NodeVisitor {

	public static final boolean DEBUGGING = true;
	// TODO: Once all the code is generated, update all places that currently rely on this crappy hard-coded constant.
	private static final int FIRST_ADDRESS_IN_STACK = 500;

	private CodeWriter writer;

	private SymbolTable symbolTable;
	
	/**
	 * The a variable set while traversing the tree that keeps track of the current declaration type.
	 */
	private PrimitiveSemType currentDeclarationType;
	
	public CodeGenVisitor() {
		this.symbolTable = new CodeGenSymbolTable();
		this.symbolTable.Initialize();
	}

	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		this.writer = new CodeWriter();
		program.accept(this);
		this.writer.printWrittenCode();	
		if (!this.writer.isCompletelyPatched()) {
			Main.errorOccurred = true;
			System.out.print("ERROR: We have unpatched branch statements! Please fix them!");
		}

		Machine.setPC((short)0); /* where code to be executed begins */
		Machine.setMSP(this.writer.getCurrentProgramCounter()); /* where memory stack begins */
		Machine.setMLP((short)(Machine.memorySize - 1)); /* */
	}
	
	// --- Program ---

	@Override
	public void visit(Program visitable) {
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TRON);
		}
		
		// Open a new scope.
		this.symbolTable.openScope(ScopeType.PROGRAM);
		SymScope scope = this.symbolTable.getCurrentScope();
		
		// Write out to the display.
		this.writer.writeRawAssembly(Machine.PUSH, FIRST_ADDRESS_IN_STACK);
		this.writer.writeRawAssembly(Machine.SETD, scope.getLexicalLevel());
		
		this.symbolTable.closeCurrentScope();
		
		super.visit(visitable);
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TROFF);
		}
		writer.writeRawAssembly(Machine.HALT);
	}
	
	// --- Declarations ---
	
	@Override
	public void visit(ScalarDeclPart visitable) {
		String varName = visitable.getName();
		PrimitiveSemType varType = this.currentDeclarationType;
		
		Symbol newSymbol = this.symbolTable.addSymbolToCurScope(varName, varType);
		SymScope scope = this.symbolTable.getCurrentScope();
		newSymbol.setOffset(scope.assignSpaceForNewVariable(1));
	}
	
	@Override
	public void visit(MultiDeclarations visitable) {
		// Keep track of the declaration state and use it later.
		this.currentDeclarationType = visitable.getType().getSemanticType();
		
		super.visit(visitable);
		
		this.currentDeclarationType = null;
	}
	
	@Override
	public void visit(ScalarDecl visitable) {
		Symbol newSymbol = this.symbolTable.addSymbolToCurScope(visitable.getName(), visitable.getType().getSemanticType());
		SymScope scope = this.symbolTable.getCurrentScope();
		newSymbol.setOffset(scope.assignSpaceForNewVariable(1));		
	}
	
	@Override
	public void visit(RoutineDecl visitable) {	
		AddressPatch endOfFunctionDefinitionPatch = this.writer.writePatchableBranchAlways();
		this.symbolTable.openScope(ScopeType.ROUTINE);
		
		RoutineSemType routineType = new RoutineSemType(visitable.getType().getSemanticType());
		this.symbolTable.addSymbolToCurScope(visitable.getName(), routineType);
		
		// The following values are pushed on from the caller.
		// - Return value space
		// - The line of code to return to
		// - The value in the display
		// - The parameters
		
		// --- Prologue.		
		
		// Set the display register.
		short partialActivationRecordSize = 10; // TODO
		this.writer.writeRawAssembly(Machine.PUSHMT);
		this.writer.writeRawAssembly(Machine.PUSH, partialActivationRecordSize);
		this.writer.writeRawAssembly(Machine.SUB);
		this.writer.writeRawAssembly(Machine.SETD, this.symbolTable.getCurrentScope().getLexicalLevel());
		
		// Push space for local variables on stack.
		short localVariableSize = 10; // TODO
		this.writer.writeRawAssembly(Machine.PUSH, localVariableSize);
		this.writer.writeRawAssembly(Machine.PUSH, 0); // Dummy value.
		this.writer.writeRawAssembly(Machine.DUPN);
		
		// Set the offset properly in the function scope.
		this.symbolTable.getCurrentScope().assignSpaceForNewVariable(localVariableSize + partialActivationRecordSize);
		
		// --- Visit children.
		super.visit(visitable);
		
		// --- Epilogue.
		
		// Delete the local variables.
		short localParameterSize = 5; // TODO
		this.writer.writeRawAssembly(Machine.PUSH, localVariableSize + localParameterSize);
		this.writer.writeRawAssembly(Machine.POPN);
		
		// Set the display register back; the value on the stack is the previous value for this register.
		this.writer.writeRawAssembly(Machine.SETD, this.symbolTable.getCurrentScope().getLexicalLevel());

		// Jump to back to where this method was called.
		this.writer.writeRawAssembly(Machine.BR);
		
		// The return value is left on the stack for use.
		// TODO: Do we pop this for procedures?
		if (visitable.getType() == null) {
			this.writer.writeRawAssembly(Machine.POP);
		}
		
		
		this.writer.patchAddress(endOfFunctionDefinitionPatch);		
		this.symbolTable.closeCurrentScope();
	}
	
	@Override
	public void visit(ArrayDeclPart visitable) {
		String varName = visitable.getName();
		
		ArraySemType varType = 
				new ArraySemType(this.currentDeclarationType,
								 visitable.getLowerBoundary1(),
								 visitable.getUpperBoundary1(),
								 visitable.getLowerBoundary2(),
								 visitable.getUpperBoundary2());
		
		Symbol newSymbol = this.symbolTable.addSymbolToCurScope(varName, varType);
		SymScope scope = this.symbolTable.getCurrentScope();
		newSymbol.setOffset(scope.assignSpaceForNewVariable(varType.getSize()));
	}
	
	// --- Statements ---

	public void visit(AssignStmt visitable) {
		// Get the address of the left expression and push it onto the stack.
		//TODO: handle arrays.
		ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer);
		visitable.getLval().accept(addressVisitor);
		
		// Get the value of the right expression and push it onto the stack.
		visitable.getRval().accept(this);
		
		// Store the value at the address.
		this.writer.writeRawAssembly(Machine.STORE);
	}
	
	@Override
	public void visit(IfStmt visitable) {
		// DO NOT CALL SUPER
		// TODO: Handle conditional AND and OR!
		
		// Leaves the result on top of the stack.
		visitable.getCondition().accept(this);
		
		// If false, we want to jump to the end of the IF-block or inside the ELSE-block.
		AddressPatch patchIf = this.writer.writePatchableBranchIfFalse();
		
		for (Stmt trueStmt : visitable.getWhenTrue()) {
			trueStmt.accept(this);
		}

		// If there is no else, then "whenFalse" will be null.
		if (visitable.getWhenFalse() == null) {
			// Patch up the IF-block to jump here when false.
			this.writer.patchAddress(patchIf, this.writer.getCurrentProgramCounter());
		} else {
			AddressPatch pathToEnd = this.writer.writePatchableBranchAlways();
			this.writer.patchAddress(patchIf, this.writer.getCurrentProgramCounter());
			
			for (Stmt falseStmt : visitable.getWhenFalse()) {
				falseStmt.accept(this);
			}
			this.writer.patchAddress(pathToEnd, this.writer.getCurrentProgramCounter());
		}
	}
	
	@Override
	public void visit(PutStmt visitable) {
		// DO NOT CALL SUPER
		
		for (Printable printable : visitable.getOutputs()) {
			if (printable instanceof TextConstExpn) {
				// Handle special case of printing text constants.
				TextConstExpn expn = (TextConstExpn)printable;
				for(char c : expn.getValue().toCharArray()) {
					this.writer.writeRawAssembly(Machine.PUSH, c);
					this.writer.writeRawAssembly(Machine.PRINTC);
				}
			} else if (printable instanceof SkipConstExpn) {
				// Handle special case of printing newlines.
				this.writer.writeRawAssembly(Machine.PUSH, '\n');
				this.writer.writeRawAssembly(Machine.PRINTC);
			} else if (printable instanceof Expn) {
				// Handle regular expressions.
				Expn expn = (Expn)printable;
				expn.accept(this);
				this.writer.writeRawAssembly(Machine.PRINTI);
			} else {
				System.out.println("WARNING: Cannot handle printing non-experssion!");
			}
		}
	}
	
	// --- Expressions ---
	
	@Override
	public void visit(AnonFuncExpn visitable) {
		// TODO
		super.visit(visitable);
	}
	
	@Override
	public void visit(ArithExpn visitable) {
		super.visit(visitable);
		
		String s = visitable.getOpSymbol();
		if (s.equals("+")) {
			this.writer.writeRawAssembly(Machine.ADD);
		} else if (s.equals("-")) {
			this.writer.writeRawAssembly(Machine.SUB);
		} else if (s.equals("*")) {
			this.writer.writeRawAssembly(Machine.MUL);
		} else if (s.equals("/")) {
			this.writer.writeRawAssembly(Machine.DIV);
		} else { 
			System.out.println("WARNING: Encountered bad symbol.");
		}
	}
	
	@Override
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);		
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
	}
	
	@Override
	public void visit(BoolExpn visitable) {
		// Do conditional evaluation!
		
		// Evaluate the first expression.
		visitable.getFirstExpression().accept(this);
		
		if (visitable.getOpSymbol().equals("|")) {
			// If we see "true", we don't want to evaluate the second expression.
			AddressPatch toNextExpnPatch = this.writer.writePatchableBranchIfFalse();
			
			// So, we have seen "true", lets push "true" back on the track and jump to the end. 
			this.writer.writeRawAssembly(Machine.PUSH, Machine.MACHINE_TRUE);
			AddressPatch toEndPatch = this.writer.writePatchableBranchAlways();
			
			// So, we have seen "false", lets evaluate the next expression and leave that.
			this.writer.patchAddress(toNextExpnPatch);
			visitable.getSecondExpression().accept(this);
			
			// Now, we are done evaluating the second expression.
			this.writer.patchAddress(toEndPatch);	
			
		} else if (visitable.getOpSymbol().equals("&")) {
			// If we see "false", we don't want to evaluate the second expression.
			AddressPatch toNextExpnPatch = this.writer.writePatchableBranchIfFalse();
			
			// So, we have seen "true", lets evaluate the next expression.
			visitable.getSecondExpression().accept(this);
			AddressPatch toEndPatch = this.writer.writePatchableBranchAlways();
			
			// So, we have seen "false", lets just push that and get out!
			this.writer.patchAddress(toNextExpnPatch);
			this.writer.writeRawAssembly(Machine.PUSH, Machine.MACHINE_FALSE);
			
			// Now, we are done evaluating the second expression.
			this.writer.patchAddress(toEndPatch);		
			
		} else {
			System.out.println("WARNING: Encountered bad symbol.");
		}
	}
	
	@Override
	public void visit(IntConstExpn visitable) {
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue());
	}
	
	@Override
	public void visit(IdentExpn visitable) {
		String varName = visitable.getIdentifier();
		
		// Push the address of the variable on to the top of the stack. 
		// Use an address visitor to do this.
		ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer, this);
		visitable.accept(addressVisitor);
		
		// Load the value at that address on to the top of the stack.
		this.writer.writeRawAssembly(Machine.LOAD);
	}
	
	@Override
	public void visit(SkipConstExpn visitable) {
		// Handled by PutStmt
	}
	
	@Override
	public void visit(SubsExpn visitable) {
		// Evaluate the left and right subscripts, pushing the results on to the stack.
		visitable.getSubscript1().accept(this);
		visitable.getSubscript2().accept(this);
		
		// Do magic math to get an offset from the beginning of the address of the variable.
		
		
	}
	
	@Override
	public void visit(TextConstExpn visitable) {
		// Handled by PutStmt
	}
}

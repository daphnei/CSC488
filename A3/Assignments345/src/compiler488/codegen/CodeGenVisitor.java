package compiler488.codegen;

import compiler488.ast.Printable;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.Stmt;
import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;
import compiler488.compiler.Main;
import compiler488.exceptions.ExecutionException;
import compiler488.exceptions.MemoryAddressException;
import compiler488.runtime.Machine;
import compiler488.semantics.NodeVisitor;
import compiler488.semantics.types.BooleanSemType;
import compiler488.semantics.types.IntegerSemType;
import compiler488.semantics.types.PrimitiveSemType;
import compiler488.semantics.types.SemType;
import compiler488.symbol.CodeGenSymbolTable;
import compiler488.symbol.ScopeType;
import compiler488.symbol.SymScope;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

public class CodeGenVisitor extends NodeVisitor {

	//TODO: Once all the code is generated, update all places that currently rely on this crappy hard-coded constant.
	private static final int FIRST_ADDRESS_IN_STACK = 500;
	
	public static final boolean DEBUGGING = false;

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

	@Override
	public void visit(Program visitable) {
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TRON);
		}
		
		//Open a new scope.
		this.symbolTable.openScope(ScopeType.PROGRAM);
		SymScope scope = this.symbolTable.getCurrentScope();
		
		// Write out to the display.
		this.writer.writeRawAssembly(Machine.PUSH, FIRST_ADDRESS_IN_STACK);
		this.writer.writeRawAssembly(Machine.SETD, scope.getLexicalLevel());
		
		super.visit(visitable);
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TROFF);
		}
		writer.writeRawAssembly(Machine.HALT);
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
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);		
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
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
		ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer);
		visitable.accept(addressVisitor);
		
		// Load the value at that address on to the top of the stack.
		this.writer.writeRawAssembly(Machine.LOAD);
	}

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
		// HACK: Keep track of the declaration state and use it later.
		this.currentDeclarationType = visitable.getType().getSemanticType();
		
		super.visit(visitable);
		
		this.currentDeclarationType = null;
	}
}

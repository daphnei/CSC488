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
import compiler488.ast.expn.EqualsExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Stmt;
import compiler488.compiler.Main;
import compiler488.exceptions.runtime.ExecutionException;
import compiler488.exceptions.runtime.MemoryAddressException;
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
	private static final short DISPLAY_OFFSET_IN_CONTROL_BLOCK = 2;
	private static final short CONTROL_BLOCK_RETURN_VALUE = 0;
	private static final short CONTROL_BLOCK_RETURN_ADDRESS = 1;
	private static final short CONTROL_BLOCK_DISPLAY = 2;
	public static final short CONTROL_BLOCK_SIZE = 3;


	/**
	 * A writer that keeps track of the program counter and provides a consistent interface for
	 * writing to the Machine.
	 */
	private CodeWriter writer;

	/**
	 * A symbol table that holds all the identifiers in scopes as we traverse the AST generating
	 * code.
	 */
	private SymbolTable symbolTable;

	/**
	 * The a variable set while traversing the tree that keeps track of the current declaration
	 * type.
	 */
	private PrimitiveSemType currentDeclarationType;

	public CodeGenVisitor() {
		this.symbolTable = new CodeGenSymbolTable();
		this.symbolTable.Initialize();
	}

	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		this.writer = new CodeWriter(this.symbolTable);
		program.accept(this);
		this.writer.printWrittenCode();
		if (!this.writer.isCompletelyPatched()) {
			Main.errorOccurred = true;
			System.out.print("ERROR: We have unpatched branch statements! Please fix them!");
		}

		Machine.setPC((short) 0); /* where code to be executed begins */
		Machine.setMSP(this.writer.getCurrentProgramCounter()); /* where memory stack begins */
		Machine.setMLP((short) (Machine.memorySize - 1)); /* */
	}

	// --- Program ---

	@Override
	public void visit(Program visitable) {
		// Begin code generation...
		if (DEBUGGING) {
			System.out.println("START CODE GENERATION...");
			writer.writeBeginDebug();
		}

		// Open a new scope.
		this.symbolTable.openScope(ScopeType.PROGRAM);
		SymScope scope = this.symbolTable.getCurrentScope();

		// TODO: Replace this with the regular activation block.
		AddressPatch stackStartPatch = this.writer.writePatchablePush();
		this.writer.writeRawAssembly(Machine.SETD, scope.getLexicalLevel());

		this.writer.writeRawAssembly(Machine.PUSH, 0); // Push return value
		this.writer.writeRawAssembly(Machine.PUSH, 0); // Push return address
		this.writer.writeRawAssembly(Machine.ADDR, (short)0, 0); // Push display value.


		scope.assignSpaceForNewVariable(3); // Don't start assigning on top of the control block.

		// TODO: Push some fake space for holding all the variables.
		this.writer.writeRawAssembly(Machine.PUSH, 0);
		this.writer.writeRawAssembly(Machine.PUSH, 3);
		this.writer.writeRawAssembly(Machine.DUPN);

		// Generate code for the rest of the program.
		super.visit(visitable);

		// Finish up the program.
		this.symbolTable.closeCurrentScope();
		if (DEBUGGING) {
			writer.writeEndDebug();
		}
		writer.writeRawAssembly(Machine.HALT);		


		// Set the start address of the stack.
		writer.patchAddress(stackStartPatch);
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
		// Allow the
		AddressPatch endOfFunctionDefinitionPatch = this.writer.writePatchableBranchAlways();

		RoutineSemType routineType = new RoutineSemType(visitable.getType().getSemanticType());
		routineType.setStartAddress(this.writer.getCurrentProgramCounter());
		routineType.setLexicalLevel((short) (this.symbolTable.getCurrentScope().getLexicalLevel() + 1));
		this.symbolTable.addSymbolToCurScope(visitable.getName(), routineType);

		// The following values are pushed on from the caller.
		// - Return value space
		// - The line of code to return to
		// - The value in the display
		// - The parameters

		// --- Prologue.
		// TODO: Refactor into helper.

		// Set the display register.
		short controlBlockAndParameters = 4; // TODO
		this.writer.writeRawAssembly(Machine.PUSHMT);
		this.writer.writeRawAssembly(Machine.PUSH, controlBlockAndParameters);
		this.writer.writeRawAssembly(Machine.SUB);
		this.writer.writeRawAssembly(Machine.SETD, this.symbolTable.getCurrentScope().getLexicalLevel());

		// Push space for local variables on stack.
		short localVariableSize = 3; // TODO
		this.writer.writeRawAssembly(Machine.PUSH, 0); // Dummy value.
		this.writer.writeRawAssembly(Machine.PUSH, localVariableSize);
		this.writer.writeRawAssembly(Machine.DUPN);

		// Set the offset properly in the function scope.
		this.symbolTable.getCurrentScope().assignSpaceForNewVariable(CONTROL_BLOCK_SIZE);

		// --- Visit children.
		super.visit(visitable);

		// --- Epilogue.
		// TODO: Refactor into helper.

		// Delete the local variables.
		short localParameterSize = 1; // TODO
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
		// DO NOT CALL SUPER

		// Get the address of the left expression and push it onto the stack.
		// TODO: handle arrays.
		ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer, this);
		visitable.getLval().accept(addressVisitor);

		// Get the value of the right expression and push it onto the stack.
		visitable.getRval().accept(this);

		// Store the value at the address.
		this.writer.writeRawAssembly(Machine.STORE);
	}

	@Override
	public void visit(IfStmt visitable) {
		// DO NOT CALL SUPER

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
				this.writer.writeString(expn.getValue());
			} else if (printable instanceof SkipConstExpn) {
				// Handle special case of printing newlines.
				this.writer.writeRawAssembly(Machine.PUSH, '\n');
				this.writer.writeRawAssembly(Machine.PRINTC);
			} else if (printable instanceof Expn) {
				// Handle regular expressions.
				Expn expn = (Expn) printable;
				expn.accept(this);
				this.writer.writeRawAssembly(Machine.PRINTI);
			} else {
				System.out.println("WARNING: Cannot handle printing non-experssion!");
			}
		}
	}

	@Override
	public void visit(ReturnStmt visitable) {
		if (visitable.getValue() != null) {
			// Put the return value into the control block.
			this.writer.writeRawAssembly(Machine.ADDR, this.symbolTable.getCurrentScope().getLexicalLevel(), CONTROL_BLOCK_RETURN_VALUE);
			visitable.getValue().accept(this);
			this.writer.writeRawAssembly(Machine.STORE);
		}

		// TODO: Do jumping.
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
		if (s.equals(ArithExpn.OP_PLUS)) {
			this.writer.writeRawAssembly(Machine.ADD);
		} else if (s.equals(ArithExpn.OP_MINUS)) {
			this.writer.writeRawAssembly(Machine.SUB);
		} else if (s.equals(ArithExpn.OP_TIMES)) {
			this.writer.writeRawAssembly(Machine.MUL);
		} else if (s.equals(ArithExpn.OP_DIVIDE)) {
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

		} else if (visitable.getOpSymbol().equals(BoolExpn.OP_AND)) {
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
	public void visit(FunctionCallExpn visitable) {
		// TODO: Refactor into helper.

		// Push space for the return value.
		this.writer.writeRawAssembly(Machine.PUSH, 0);

		// Push the return address.
		AddressPatch afterFunctionCallPatch = this.writer.writePatchablePush();

		// Push the value of the current display onto the stack.
		this.writer.writeRawAssembly(
				Machine.ADDR,
				this.symbolTable.getCurrentScope().getLexicalLevel(),
				CONTROL_BLOCK_DISPLAY);
		this.writer.writeRawAssembly(Machine.LOAD);

		// Branch to the caller.
		Symbol symbol = this.symbolTable.retrieveSymbol(visitable.getIdentifier());
		RoutineSemType routine = (RoutineSemType)symbol.getType();
		this.writer.writeBranchAlways(routine.getStartAddress());

		// Patch now that we know were to come back to after calling the function.
		this.writer.patchAddress(afterFunctionCallPatch);
	}

	@Override
	public void visit(EqualsExpn visitable) {
		super.visit(visitable);
		this.writer.writeRawAssembly(Machine.EQ);
		if (visitable.getOpSymbol().equals(EqualsExpn.OP_NOT_EQUAL)) {
			this.writer.writeNot();
		}
	}

	@Override
	public void visit(IntConstExpn visitable) {
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue());
	}

	@Override
	public void visit(IdentExpn visitable) {
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
		// First visit this expression withing the address visitor in order
		// to get the address of the desired position in the array.
		ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer, this);
		visitable.accept(addressVisitor);

		// Load the value at that address on to the top of the stack.
		this.writer.writeRawAssembly(Machine.LOAD);
	}

	@Override
	public void visit(TextConstExpn visitable) {
		// Handled by PutStmt
	}
}

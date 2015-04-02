package compiler488.codegen;

import compiler488.ast.ASTList;
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
import compiler488.ast.expn.CompareExpn;
import compiler488.ast.expn.EqualsExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.NotExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.expn.UnaryMinusExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.LoopStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.GetStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Stmt;
import compiler488.ast.stmt.WhileDoStmt;
import compiler488.ast.stmt.ProcedureCallStmt;
import compiler488.ast.type.Type;
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
import java.util.ListIterator;

public class CodeGenVisitor extends NodeVisitor {

	/**
	 * Whether or not to enable debugging for the entire program execution.
	 */
	public static final boolean DEBUGGING = false;
	
	/**
	 * Offsets from the LL for the pieces of info in the activation record.
	 */
	public static final short CONTROL_BLOCK_RETURN_VALUE = 0;
	public static final short CONTROL_BLOCK_RETURN_ADDRESS = 1;
	public static final short CONTROL_BLOCK_DISPLAY = 2;
	public static final short CONTROL_BLOCK_SIZE = 3;
	
	/**
	 * The number of anonymous functions that have been created so far. This
	 * var is used so that we can give a unique identifier to each of them.
	 */
	public static int anonCount = 0;

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
		// Set up the symbol table.
		this.symbolTable = new CodeGenSymbolTable();
		this.symbolTable.Initialize();
	}

	// Start off the code generation.
	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		this.writer = new CodeWriter();
		
		// Start the traversal of the tree.
		program.accept(this);
		
		// Print out a copy of all of that that was genrated.
		this.writer.printWrittenCode();
		if (!this.writer.isCompletelyPatched()) {
			Main.errorOccurred = true;
			System.out.print("ERROR: We have unpatched branch statements! Please fix them!");
		}

		// Set up the stack pointer and the program counter so that execution can begin.
		Machine.setPC((short) 0); /* where code to be executed begins */
		Machine.setMSP(this.writer.getProgramCounter()); /* where memory stack begins */
		Machine.setMLP((short) (Machine.memorySize - 1)); /* where the memory stack ends */
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

		// Set the initial display to the correct value.
		AddressPatch stackStartPatch = this.writer.writePatchablePush();
		this.writer.writeRawAssembly(Machine.SETD, scope.getLexicalLevel());

		// Write a dummy control block.
		AddressPatch returnAddressPatch = this.writer.writeControlBlock(scope.getLexicalLevel());
		this.writer.patchAddress(returnAddressPatch, Machine.UNDEFINED);  

		// Push some fake space for holding all program scope variables.
		this.writer.writeRawAssembly(Machine.PUSH, Machine.UNDEFINED);
		AddressPatch amountOfVariableSpacePatch = this.writer.writePatchablePush();
		this.writer.writeRawAssembly(Machine.DUPN);

		// Generate code for the rest of the program.
		super.visit(visitable);

		// Finish up the program.
		this.writer.patchAddress(amountOfVariableSpacePatch, this.symbolTable.getCurrentScope().getSpaceAllocatedForVariables());
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
		SymScope scope = this.symbolTable.getCurrentMajorScope();
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
		SymScope scope = this.symbolTable.getCurrentMajorScope();
		newSymbol.setOffset(scope.assignSpaceForNewVariable(1));
		// These declarations are only in routines... So this should be safe.
		scope.getRoutine().addParameter(visitable.getType().getSemanticType());
	}

	@Override
	public void visit(RoutineDecl visitable) {
		// DO NOT CALL SUPER
		
		// Allow us to jump over the routine when we are not running it.
		AddressPatch endOfFunctionDefinitionPatch = this.writer.writePatchableBranchAlways();

	    PrimitiveSemType semType = null;
		Type type = visitable.getType();
		if (type != null)
		    semType = type.getSemanticType();
		
		RoutineSemType routineType = new RoutineSemType(semType);
		routineType.setStartAddress(this.writer.getProgramCounter());
		routineType.setLexicalLevel((short) (this.symbolTable.getCurrentScope().getLexicalLevel() + 1));
		this.symbolTable.addSymbolToCurScope(visitable.getName(), routineType);

		// Open a new scope for the routine.
		this.symbolTable.openScope(ScopeType.ROUTINE);
		this.symbolTable.getCurrentScope().setRoutine(routineType);

		// Get the parameters for this routine.
		for (ScalarDecl param : visitable.getParameters()) {
			param.accept(this);
		}
		
		// Do the setup, execution, and tear-down.
		AddressPatch localMinusParamsSizePatch = this.writer.writeRoutineDeclareSetup(
				routineType);
		visitable.getBody().accept(this);
		
		short pcBeforeTeardown = this.writer.getProgramCounter();
		
		this.writer.writeRoutineDeclareTeardown(
				routineType,
				this.symbolTable.getCurrentScope().getSpaceAllocatedForVariables(),
				localMinusParamsSizePatch);
	
		// Patch the address for the end of the function definition
		this.writer.patchAddress(endOfFunctionDefinitionPatch);
		
		// Also patch any returns found in this scope.
		this.writer.patchReturnsOrExits(this.symbolTable.getCurrentMajorScope(), pcBeforeTeardown);
		
		// Close the scope.
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
		SymScope scope = this.symbolTable.getCurrentMajorScope();
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
			this.writer.patchAddress(patchIf, this.writer.getProgramCounter());
		} else {
			AddressPatch pathToEnd = this.writer.writePatchableBranchAlways();
			this.writer.patchAddress(patchIf, this.writer.getProgramCounter());

			for (Stmt falseStmt : visitable.getWhenFalse()) {
				falseStmt.accept(this);
			}
			this.writer.patchAddress(pathToEnd, this.writer.getProgramCounter());
		}
	}

	public void visit(WhileDoStmt visitable) {		
		// Write the program counter value to the stack.
		int topOfTheLoop = this.writer.getProgramCounter();

                this.symbolTable.openScope(ScopeType.LOOP);
                
		// Check the condition
		Expn condition = visitable.getExpn();
		AddressPatch conditionPatch = null;
		condition.accept(this);

		//If the condition has not been met, branch to end.
		conditionPatch = this.writer.writePatchableBranchIfFalse();

		// Generate code for all my children.
		for (Stmt bodyStmt : visitable.getBody()) {
			bodyStmt.accept(this);
		}

		// Branch back to the top of the loop.
		this.writer.writeRawAssembly(Machine.PUSH, topOfTheLoop);
		this.writer.writeRawAssembly(Machine.BR);
                
		// Make sure that a false condition will push execution to outside the program outside of the loop.
		this.writer.patchAddress(conditionPatch, this.writer.getProgramCounter());	
		
		// Patch any exits that may have occured in the body of the loop.
		this.writer.patchReturnsOrExits(this.symbolTable.getCurrentLoopScope(), this.writer.getProgramCounter());
                
                this.symbolTable.closeCurrentScope();
	}
	
	@Override
	public void visit(LoopStmt visitable) {
		// Write the program counter value to the stack.
		int topOfTheLoop = this.writer.getProgramCounter();

		this.symbolTable.openScope(ScopeType.LOOP);
		
		// Generate code for all my children.
		for (Stmt bodyStmt : visitable.getBody()) {
			bodyStmt.accept(this);
		}

		// Branch back to the top of the loop.
		this.writer.writeRawAssembly(Machine.PUSH, topOfTheLoop);
		this.writer.writeRawAssembly(Machine.BR);
		
		// Patch any exits.
		this.writer.patchReturnsOrExits(this.symbolTable.getCurrentLoopScope(), this.writer.getProgramCounter());
		
		this.symbolTable.closeCurrentScope();
	}

	public void visit(ExitStmt visitable) {
                AddressPatch patch;
                if (visitable.getExpn()==null){
                    patch = this.writer.writePatchableBranchAlways();
                } else{
                    visitable.getExpn().accept(this);
                    this.writer.writeNot();
                    patch = this.writer.writePatchableBranchIfFalse();
                }
		this.symbolTable.getCurrentLoopScope().keepTrackOfAnExit(patch);
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
				System.out.println("WARNING: Cannot handle printing non-expression!");
			}
		}
	}
	
	@Override
	public void visit(GetStmt visitable) {
	    
	    for (Expn expn : visitable.getInputs()) {
	        
	        ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer, this);
	        expn.accept(addressVisitor);
	        
	        //expn.accept(this);  //not sure if this will cause problems b/c of LOAD
	        //this.writer.writeRawAssembly(Machine.POP); // temp HACK
	        this.writer.writeRawAssembly(Machine.READI);
	        this.writer.writeRawAssembly(Machine.STORE);
	        
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

		AddressPatch patch = this.writer.writePatchableBranchAlways();
		this.symbolTable.getCurrentMajorScope().keepTrackOfAnExit(patch);
	}
	
	
	@Override
	public void visit(ProcedureCallStmt visitable)
	{
	    Symbol symbol = this.symbolTable.retrieveSymbol(visitable.getIdentifier());
	    ASTList<Expn> args = visitable.getArguments();
	        
	    visitHelperFuncProc(symbol, args);
	    
	}
	
	// --- Expressions ---

	@Override
	public void visit(AnonFuncExpn visitable) {
                // DO NOT CALL SUPER
                // use a fakename template for anonfuncs
                String name = "anonfunc"+anonCount;
                anonCount+=1;
                
                //create a fake routine declaration
                ASTList<Stmt> stmt = visitable.getBody();
                stmt.add(new ReturnStmt(visitable.getExpn(), 0, 0));
                Scope scope = new Scope(stmt,0,0);
                Type type = visitable.getExpn().getResultType().returnAST();
                RoutineDecl fakefunction = new RoutineDecl(name,type,scope,0,0);
                this.visit(fakefunction);
                
                //create a fake call of the  function
                FunctionCallExpn fakecall = new FunctionCallExpn(name, new ASTList<Expn>(), 0, 0);
                this.visit(fakecall);
	}

	@Override
	public void visit(ArithExpn visitable) {
		super.visit(visitable);

		// These arithmatic expressions are all pretty straightforward. 
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

	public void visit(CompareExpn visitable) {
		super.visit(visitable);
		
		String s = visitable.getOpSymbol();
		if (s.equals(CompareExpn.OP_GREATER)) {
			// a > b is the same as b < a
			this.writer.writeRawAssembly(Machine.SWAP);
			this.writer.writeRawAssembly(Machine.LT);
		} 
		else if (s.equals(CompareExpn.OP_LESS)) {
			// this is the case we have a direction machine isntruction for.
			this.writer.writeRawAssembly(Machine.LT);
		}
		else if (s.equals(CompareExpn.OP_GREATER_EQUAL)) {
			// a >= b is the same as !(b < a) 
			this.writer.writeRawAssembly(Machine.LT);
			this.writer.writeNot();
		}
		else if (s.equals(CompareExpn.OP_LESS_EQUAL)) {
			// a <= b is the same as !(b < a)
			this.writer.writeRawAssembly(Machine.SWAP);
			this.writer.writeRawAssembly(Machine.LT);
			this.writer.writeNot();
		}
	}//abab
	
	@Override
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);
		
		// Push either machine true or false depending on the value of the constant.
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
	}

	@Override
	public void visit(BoolExpn visitable) {
		// Do conditional evaluation!

		// Evaluate the first expression.
		visitable.getFirstExpression().accept(this);

		if (visitable.getOpSymbol().equals(BoolExpn.OP_OR)) {
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
	public void visit(EqualsExpn visitable) {
		super.visit(visitable);
		
		// Check for equality.
		this.writer.writeRawAssembly(Machine.EQ);
		
		// Add in a "not" if necessary.
		if (visitable.getOpSymbol().equals(EqualsExpn.OP_NOT_EQUAL)) {
			this.writer.writeNot();
		}
	}

	@Override
	public void visit(FunctionCallExpn visitable) {
		// Get the symbol corresponding to the function call.
	    Symbol symbol = this.symbolTable.retrieveSymbol(visitable.getIdentifier());
	    ASTList<Expn> args = visitable.getArguments();
	    
	    visitHelperFuncProc(symbol, args);
	}
	
	
	public void visitHelperFuncProc(Symbol symbol, ASTList<Expn> args) {

        RoutineSemType routine = (RoutineSemType) symbol.getType();

        // Write the control block.
        AddressPatch returnAddressPatch = this.writer.writeControlBlock(routine.getLexicalLevel());

        // Push parameters onto the stack.
        for (Expn expn : args) {
            expn.accept(this);
        }

        // Branch to the caller.
        this.writer.writeBranchAlways(routine.getStartAddress());

        // Patch now that we know were to come back to after calling the function.
        this.writer.patchAddress(returnAddressPatch); 
        
		// The return value is left on the stack for use; unless this is a procedure.
		if (routine.getReturnType() == null) {
			this.writer.writeRawAssembly(Machine.POP);
		}
	}
	

	public void visit(IntConstExpn visitable) {
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue());
	}

	@Override
	public void visit(IdentExpn visitable) {
		// Push the address of the variable on to the top of the stack.
		// Use an address visitor to do this.
		Symbol symbol = this.symbolTable.retrieveSymbol(visitable.getIdentifier());
		if (symbol.getType() instanceof RoutineSemType) {
			this.visitHelperFuncProc(symbol, new ASTList<Expn>());
		} else {
			ExpnAddressVisitor addressVisitor = new ExpnAddressVisitor(this.symbolTable, this.writer, this);
			visitable.accept(addressVisitor);

			// Load the value at that address on to the top of the stack.
			this.writer.writeRawAssembly(Machine.LOAD);
		}
	}


	@Override
	public void visit(NotExpn visitable) {
		super.visit(visitable);

		this.writer.writeNot();
	}

	@Override
	public void visit(UnaryMinusExpn visitable) {
		super.visit(visitable);

		this.writer.writeRawAssembly(Machine.NEG);
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

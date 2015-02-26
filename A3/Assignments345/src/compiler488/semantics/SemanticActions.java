package compiler488.semantics;

import java.util.ArrayList;
import java.util.Stack;

import compiler488.ast.ASTList;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.AnonFuncExpn;
import compiler488.ast.expn.BinaryExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.ProcedureCallStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.type.Type;
import compiler488.exceptions.InvalidArrayBoundsException;
import compiler488.exceptions.InvalidNumberOfArgumentsException;
import compiler488.exceptions.NotArrayException;
import compiler488.exceptions.SemanticErrorException;
import compiler488.exceptions.UndeclaredSymbolException;
import compiler488.interfaces.IIdentifier;
import compiler488.interfaces.IRoutineCall;
import compiler488.interfaces.IVisitableElement;
import compiler488.semantics.types.ArraySemType;
import compiler488.semantics.types.BooleanSemType;
import compiler488.semantics.types.ErrorSemType;
import compiler488.semantics.types.IntegerSemType;
import compiler488.semantics.types.PrimitiveSemType;
import compiler488.semantics.types.RoutineSemType;
import compiler488.semantics.types.SemType;
import compiler488.symbol.ScopeType;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

public class SemanticActions {

	public static final String NULL_RESULT_TYPE_EXCEPTION = "Cannot evaluate expression. (COULD INDICATE MISSING RESULT TYPE SET)";

	private Semantics semantics;
	private SymbolTable table;

	public Stack<RoutineSemType> openRoutines;

	public SemanticActions(Semantics semantics, SymbolTable table) {
		this.table = table;
		this.semantics = semantics;

		this.openRoutines = new Stack<RoutineSemType>();
	}

	public void checkSemanticRule(int action, IVisitableElement element) throws SemanticErrorException {
		switch (action) {

		// Scopes and Program

		case 0: // Open program scope.
			table.openScope(ScopeType.PROGRAM);
			break;

		case 4: // Open function scope.
		case 8: // Open procedure scope.
			table.openScope(ScopeType.ROUTINE);
			break;

		case 1: // End all scopes.
		case 7: // End ordinary scope.
			table.closeCurrentScope();
			break;
		case 5: // End function scope
		case 9: // End procedure scope
			table.closeCurrentScope();
			this.openRoutines.pop();
			break;

		case 3: // Associate statements and declarations with scope.
			// Do nothing, this happens automatically.
			break;

		case 6: // Open ordinary scope.
			table.openScope(ScopeType.GENERIC);
			break;

		case 99: // Open up a new loop scope. This one was not on the sheet. We made it up.
			table.openScope(ScopeType.LOOP);
			break;
			
		// Declarations

		case 10: // Declare scalar.
			table.addSymbolToCurScope(((ScalarDeclPart) element).getName(), this.semantics.getCurrentDeclarationType());
			break;

		case 11: // Declare function with no parameters and specified type.
		case 12: // Declare function with parameters and specified type.
			this.openNewRoutine((RoutineDecl) element);
			break;

		case 13: // Associate scope with function/procedure.
			// I don't think this is necessary?
			break;

		case 14: // Set parameter count to zero.
			// This isn't necessary because we're storing a list of parameters for the routine.
			break;

		case 15: // Declare parameter.
			ScalarDecl scalarDecl = (ScalarDecl) element;
			table.addSymbolToCurScope(scalarDecl.getName(), scalarDecl.getType().getSemanticType());
			this.openRoutines.peek().addParameter(scalarDecl.getType().getSemanticType());
			break;

		case 16: // Increment parameter count by one.
			// Do nothing, this happens in S15.
			break;

		case 17: // Declare procedure with no parameters.
		case 18: // Declare procedure with parameters.
			this.openNewRoutine((RoutineDecl) element);
			break;

		case 19: // Declare array variable with specified lower and upper bounds.
			ArrayDeclPart arrayPart = (ArrayDeclPart) element;
			table.addSymbolToCurScope(arrayPart.getName(), new ArraySemType(this.semantics.getCurrentDeclarationType(), arrayPart
					.getDimensions()));
			break;

		case 46: // Check that lower bound is <= upper bound.
			checkArrayBoundsValidity((ArrayDeclPart) element);
			break;

		case 47: // Associate type with variables.
			// Do nothing, this happens automatically.
			break;

		case 48: // Declare array variable with specified upper bound.
			// Do nothing, this happens in S19.
			break;

		// Statement Checking

		case 50: // Check that exit statement is directly inside a loop.
			checkExitIsDirectlyInLoop((ExitStmt) element);
			break;

		case 51: // Check that return is inside a function
		case 52: // Check that return statement is inside a procedure.
			checkReturnIsInRoutine((ReturnStmt)element);

			// HACK: This will work here, but does it belong here?
			this.openRoutines.peek().markReturnStatement();
			break;

		case 53: // Check that a function body contains at least one return statement.
			checkRoutineHasReturnStatement();
			break;

		// Expressions

		case 20: // Set result type to boolean.
			setExpressionResultType(element, BooleanSemType.BOOLEAN);
			break;

		case 21: // Set result type to integer.
			setExpressionResultType(element, IntegerSemType.INTEGER);
			break;

		case 23: // Set result type to type of expression
			// This should happen automatically.
			break;

		case 24: // Set result type to type of anonymous function
			setExpressionResultType(element, ((AnonFuncExpn)element).getExpn().getResultType());
			break;

		case 25: // Set result type to type of parametername.
		case 26: // Set result type to type of variablename.
			this.setExpressionResultTypeFromIdentifier((IdentExpn) element);
			break;

		case 27: // Set result type to type of array element.
			this.setExpressionResultTypeFromArray((SubsExpn) element);
			break;

		case 28: // Set result type to result type of function.
			this.setExpressionResultTypeFromFunction((IIdentifier) element);
			break;

		case 30: // Check that type of expression is boolean.
			checkExpnType((Expn) element, BooleanSemType.BOOLEAN);
			break;

		case 31: // Check that type of expression or variable is integer
			checkExpnType((Expn) element, IntegerSemType.INTEGER);
			break;

		case 32: // Check that left and right operand expressions are the same
			// type
			checkBinaryExpnTypesMatch((BinaryExpn) element);
			break;

		case 34: // Check that variable and expression in assignment are the same type.
			checkAssignmentTypesMatch((AssignStmt) element);
			break;

		case 35: // Check that expression type matches the return type of enclosing function.
			checkReturnType((ReturnStmt) element);
			break;

		case 36: // Check that type of argument expression matches type of corresponding formal parameter.
			checkArgumentTypeMatch((IRoutineCall) element);
			break;

		case 37: // Check that identifier has been declared as a scalar variable.
		case 39: // Check that identifier has been declared as a parameter.
			checkIdentifierIsScalar(element);
			break;

		case 38: // Check that identifier has been declared as an array.
			checkIdentiferIsArray((SubsExpn) element);
			break;

		// Functions, procedures and arguments
		case 40: // Check that identifier has been declared as a function.
			checkIdentifierIsFunction((FunctionCallExpn) element);
			break;

		case 41: // Check that identifier has been declared as a procedure.
			checkIdentifierIsProcedure((ProcedureCallStmt) element);
			break;

		case 42: // Check that the function or procedure has no parameters.
			checkForNoParameters((IIdentifier) element);
			break;

		case 43: // Check that the number of arguments is equal to the number of formal parameters.
			checkArgumentCount((IRoutineCall) element);
			break;

		case 44: // Set the argument count to zero.
			// This is not needed since the arguments are stored as a list.
			break;

		case 45: // Increment the argument count by one.
			// This is not needed since the arguments are stored as a list.
			break;

		default:
			printTodo();
			break;
		}
	}

	private void printTodo() {
		System.out.println("TODO: Semantic action not implemented!");
	}

	/**
	 * Mark that we have entered into a new routine.
	 * @param routineDecl
	 * @throws SemanticErrorException
	 */
	private void openNewRoutine(RoutineDecl routineDecl) throws SemanticErrorException {
		Type returnType = routineDecl.getType();
		this.openRoutines.push(new RoutineSemType(returnType == null ? null : returnType.getSemanticType()));
		this.table.addSymbolToCurScope(routineDecl.getName(), this.openRoutines.peek());
	}

	private void checkExitIsDirectlyInLoop(ExitStmt element) throws SemanticErrorException {
		//Check whether there is a loop scope open.
		int firstLoopScopeIndex = this.table.searchScopesForType(ScopeType.LOOP);
		int firstRoutineScopeIndex = this.table.searchScopesForType(ScopeType.ROUTINE);
		
		//Check that there exists an open loop scope, and that that open loop scope is 
		//closer than the first open routine scope
		boolean exitIsValid = firstLoopScopeIndex >= 0 && firstLoopScopeIndex < firstRoutineScopeIndex;
		if (!exitIsValid) {
			throw new SemanticErrorException("Exit statement does not occur directly inside a loop.");
		}
	}
	
	/**
	 * Checks that the input routine call is being done with the proper paramter types.
	 * @param routineCall
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkArgumentTypeMatch(IRoutineCall routineCall) throws SemanticErrorException {
		ASTList<Expn> args = routineCall.getArguments();

		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(this.table, routineCall.getIdentifier());
		ArrayList<PrimitiveSemType> params = routineSemType.getParameters();

		checkArgumentCount(routineCall);
		for (int i = 0; i < args.size(); i++) {
			PrimitiveSemType argType = args.get(i).getResultType();
			if (!argType.equals(params.get(i))) {
				throw new SemanticErrorException("In '" + routineCall.getIdentifier() + "', parameter " + i + " is expected to be a "
						+ params.get(i) + " but a " + argType + " was provided instead");
			}
		}
	}

	/**
	 * Checks that the routine being called is being called with the right number of parameters.
	 * @param routineCall the routine being called
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkArgumentCount(IRoutineCall routineCall) throws SemanticErrorException {
		RoutineSemType routine = getRoutineSemTypeFromSymbolTable(this.table, routineCall.getIdentifier());

		int numArgsInCall = routineCall.getArguments().size();
		int numParamsInDec = routine.getNumParameters();

		if (numArgsInCall != numParamsInDec) {
			throw new InvalidNumberOfArgumentsException(routineCall.getIdentifier(), numParamsInDec, numArgsInCall);
		}
	}

	/**
	 * Checks that the routine being called is supposed to have no parameters.
	 * @param routineCall the routine being called
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkForNoParameters(IIdentifier routineCall) throws SemanticErrorException {
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(this.table, routineCall.getIdentifier());

		int numParams = routineSemType.getNumParameters();
		if (numParams != 0) {
			throw new InvalidNumberOfArgumentsException(routineCall.getIdentifier(), numParams, 0);
		}
	}

	/**
	 * Checks that the input identifier maps to a function in the symbol table.
	 * @param funCallExpn
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkIdentifierIsFunction(IIdentifier funCallExpn) throws SemanticErrorException {
		String ident = funCallExpn.getIdentifier();
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(this.table, ident);

		// Make sure it is a function and not a procedure. Functions should have a return type.
		if (routineSemType.getReturnType() == null) {
			setExpressionResultType(funCallExpn, ErrorSemType.ERROR);
			throw new SemanticErrorException("'" + ident + "' is a procedure, but a function was exptected.");
		}
	}

	/**
	 * Checks that the input identifier maps to a procedure in the symbol table.
	 * @param procCallStmt
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkIdentifierIsProcedure(ProcedureCallStmt procCallStmt) throws SemanticErrorException {
		String ident = procCallStmt.getIdentifier();
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(this.table, ident);

		// Make sure it is a procedure and not a function. Procedures should NOT have a return type.
		if (routineSemType.getReturnType() != null) {
			throw new SemanticErrorException("'" + ident + "' is a function, not a procedure was exptected.");
		}
	}

	/**
	 * Checks that the currently open routine has at least one return statement.
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkRoutineHasReturnStatement() throws SemanticErrorException {
		if (!this.openRoutines.peek().seenReturnStatement()) {
			throw new SemanticErrorException("Function or procedure is missing a return statement.");
		}
	}

	/**
	 * Checks that the return statement is inside a procedure or function.
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkReturnIsInRoutine(ReturnStmt stmt) throws SemanticErrorException {
		if (this.openRoutines.isEmpty()) {
			throw new SemanticErrorException("Call to return outside of a procedure or function.");
		} else {
			SemType seen = stmt.getValue() == null ? null : stmt.getValue().getResultType(); 			
			SemType actual = this.openRoutines.peek().getReturnType();
			if (seen == null && actual != null) {
				throw new SemanticErrorException("A return must have a parameter inside a function.");
			}
			if (seen != null && actual == null) {
				throw new SemanticErrorException("A return must have no parameter inside a procedure.");
			}
		}
	}

	/**
	 * Checks that the input identifier maps to a scalar type (integer or boolean).
	 * @param element
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkIdentifierIsScalar(IVisitableElement element) throws SemanticErrorException {
		IdentExpn ident = (IdentExpn) element;
		Symbol symbol = this.table.retrieveSymbol(ident.getIdentifier());
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident.getIdentifier());
		}
	}

	/**
	 * Checks to make sure the variable being indexed into is an array.
	 * @param element
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkIdentiferIsArray(SubsExpn element) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(element.getVariable());
		if (symbol == null) {
			setExpressionResultType(element, ErrorSemType.ERROR);
			throw new UndeclaredSymbolException(element.getVariable());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof ArraySemType)) {
			setExpressionResultType(element, ErrorSemType.ERROR);
			throw new NotArrayException(element.getVariable());
		}
		
		ArraySemType array = (ArraySemType)symbol.getType();
		if (array.getDimensions() != element.getDimensions()) {
			setExpressionResultType(element, ErrorSemType.ERROR);
			throw new SemanticErrorException("Array expects " + array.getDimensions() + " dimensions but is indexed by " + element.getDimensions() + " dimensions.");
		}
	}

	/**
	 * Checks if the input array's bounds are in the proper range.
	 * @param array
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkArrayBoundsValidity(ArrayDeclPart array) throws SemanticErrorException {
		if (array.getLowerBoundary1() > array.getUpperBoundary1()) {
			throw new InvalidArrayBoundsException(array.getName());
		}
		if (array.getDimensions() == 2) {
			if (array.getLowerBoundary2() > array.getUpperBoundary2()) {
				throw new InvalidArrayBoundsException(array.getName());
			}
		}
	}

	/**
	 * Checks if the correct type is being returned in the return statement.
	 * @param returnStmt
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkReturnType(ReturnStmt returnStmt) throws SemanticErrorException {
		checkReturnIsInRoutine(returnStmt);

		if (!this.openRoutines.peek().getReturnType().equals(returnStmt.getValue().getResultType())) {
			throw new SemanticErrorException("Trying to return value of type " + returnStmt.getValue().getResultType() + " when type "
					+ this.openRoutines.peek().getReturnType() + " is required.");
		}
	}

	/**
	 * 
	 * @param assignStmt
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkAssignmentTypesMatch(AssignStmt assignStmt) throws SemanticErrorException {
		if (assignStmt.getRval().getResultType() == null || assignStmt.getLval().getResultType() == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}
		if (!assignStmt.getLval().getResultType().equals(assignStmt.getRval().getResultType())) {
			throw new SemanticErrorException("Trying to assign a value of type " + assignStmt.getRval().getResultType()
					+ " to a variable of type " + assignStmt.getLval().getResultType());
		}
	}

	/**
	 * Check that the specified expression has the specified result type.
	 * 
	 * @param expression
	 * @param resultType
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkExpnType(Expn expression, SemType resultType) throws SemanticErrorException {
		PrimitiveSemType expResultType = expression.getResultType();
		if (expResultType == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		} else if (expression.hasError()) {
			// Don't do anything. The user should already have seen a message about this error.
		} else if (!expResultType.equals(resultType)) {
			expression.setResultType(ErrorSemType.ERROR);
			throw new SemanticErrorException("Expected a " + resultType + " and found a " + expResultType + ".");
		}
	}

	/**
	 * Given a binary expressions check that the sub-expressions on the left and the right are
	 * of the same type.
	 * 
	 * @param expression
	 * @throws SemanticErrorException if the check encounters a semantic error.
	 */
	private void checkBinaryExpnTypesMatch(BinaryExpn expression) throws SemanticErrorException {
		if (expression.getFirstExpression().hasError() || expression.getSecondExpression().hasError()) {
			// Don't throw an error if either side is an error.
			return;
		}
		
		SemType firstResultType  = expression.getFirstExpression().getResultType();
		SemType secondResultType = expression.getSecondExpression().getResultType();

		if (firstResultType == null || secondResultType == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}

		boolean resultIsGood = (firstResultType.equals(secondResultType));
		System.out.println("The first has a" + firstResultType + " while the second has a " + secondResultType);
		if (!resultIsGood) {
			throw new SemanticErrorException("The left side of the expression resolves to a " + firstResultType
					+ "while the right resolves to a " + secondResultType + ".");
		}
	}

	private RoutineSemType getRoutineSemTypeFromSymbolTable(SymbolTable symbolTable, String ident) throws SemanticErrorException {
		Symbol symbol = symbolTable.retrieveSymbol(ident);
	
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident);
		} else if (!(symbol.getType() instanceof RoutineSemType)) {
			throw new SemanticErrorException("'" + ident + "' is not a function or procedure.");
		} else {
			return (RoutineSemType) symbol.getType();
		}
	}

	/**
	 * Check if the input identifier corresponds to an integer or a boolean in the symbol table.
	 * If it does, set the result type of the identifier to the one found in the symbol table.
	 * @param ident
	 * @throws SemanticErrorException if the check encountered a semantic error.
	 */
	private void setExpressionResultTypeFromIdentifier(IdentExpn ident) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(ident.getIdentifier());
		if (symbol == null) {
			setExpressionResultType(ident, ErrorSemType.ERROR);
			throw new UndeclaredSymbolException(ident.getIdentifier());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof PrimitiveSemType)) {
			setExpressionResultType(ident, ErrorSemType.ERROR);
			throw new SemanticErrorException("Trying to use non-primitive identifier in an expression.");
		}

		setExpressionResultType(ident, (PrimitiveSemType) symbol.getType());
	}

	/**
	 * Check if the input identifier corresponds to an array type, and if it does, 
	 * set the result type of that array.
	 * @param identifier The identifier that should corrspond to an array. 
	 * @throws SemanticErrorException
	 */
	private void setExpressionResultTypeFromArray(SubsExpn identifier) throws SemanticErrorException {
		checkIdentiferIsArray(identifier);
		Symbol symbol = this.table.retrieveSymbol(identifier.getVariable());
		ArraySemType array = (ArraySemType) symbol.getType();
		setExpressionResultType(identifier, array.getBaseType());
	}
	
	/**
	 * Check if the input identifier maps to a function type in the symbol table,
	 *  and it it does, set the return type of the function type to the correct value.
	 * @param identifier The identifier that should correspond to a function.
	 * @throws SemanticErrorException
	 */
	private void setExpressionResultTypeFromFunction(IIdentifier identifier) throws SemanticErrorException {
		checkIdentifierIsFunction(identifier);
		Symbol symbol = this.table.retrieveSymbol(identifier.getIdentifier());
		RoutineSemType routine = (RoutineSemType)symbol.getType();
		setExpressionResultType(identifier, routine.getReturnType());
	}

	/**
	 * Case the input element to an expression, and set its result type.
	 * @param element
	 * @param resultType
	 */
	private void setExpressionResultType(IVisitableElement element, PrimitiveSemType resultType) {
		Expn ast = (Expn) element;
		ast.setResultType(resultType);
	}
}
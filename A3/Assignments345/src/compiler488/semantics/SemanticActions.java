package compiler488.semantics;

import java.util.ArrayList;
import java.util.Stack;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import compiler488.ast.ASTList;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.BinaryExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.stmt.AssignStmt;
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
import compiler488.symbol.ArraySemType;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.RoutineSemType;
import compiler488.symbol.SemType;
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
			table.openMajorScope();
			break;

		case 4: // Open function scope.
		case 8: // Open procedure scope.
			table.openMajorScope();
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
			table.openMinorScope();
			break;

		// Declarations

		case 10: // Declare scalar.
			table.addSymbolToCurScope(((ScalarDeclPart) element).getName(), this.semantics.getCurrentDeclarationType());
			break;

		case 11: // Declare function with no parameters and specified type.
		case 12: // Declare function with parameters and specified type.
			this.openNewRoutine((RoutineDecl) element, this.table);
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
			this.openNewRoutine((RoutineDecl) element, this.table);
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
			printTodo();
			break;

		case 51: // Check that return is inside a function
		case 52: // Check that return statement is inside a procedure.
			checkReturnIsInRoutine();

			// HACK: This will work here, but does it belong here?
			this.openRoutines.peek().markReturnStatement();
			break;

		case 53: // Check that a function body contains at least one return statement.
			checkRoutineHasReturnStatement();
			break;

		// Expressions

		case 20: // Set result type to boolean.
			setExpressionResultType(element, SemType.BOOLEAN);
			break;

		case 21: // Set result type to integer.
			setExpressionResultType(element, SemType.INTEGER);
			break;

		case 23: // Set result type to type of expression
			// This should happen automatically.
			break;

		case 24: // Set result type to type of anonymous function
			printTodo(); // TODO
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
			checkExpnType((Expn) element, SemType.BOOLEAN);
			break;

		case 31: // Check that type of expression or variable is integer
			checkExpnType((Expn) element, SemType.INTEGER);
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
			checkArgumentTypeMatch((IRoutineCall) element, table);
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
			checkIdentifierIsFunction((FunctionCallExpn) element, table);
			break;

		case 41: // Check that identifier has been declared as a procedure.
			checkIdentifierIsProcedure((ProcedureCallStmt) element, table);
			break;

		case 42: // Check that the function or procedure has no parameters.
			checkForNoParameters((IIdentifier) element, table);
			break;

		case 43: // Check that the number of arguments is equal to the number of formal parameters.
			checkArgumentCount((IRoutineCall) element, table);
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

	private void openNewRoutine(RoutineDecl routineDecl, SymbolTable table) throws SemanticErrorException {
		Type returnType = routineDecl.getType();
		this.openRoutines.push(new RoutineSemType(returnType == null ? null : returnType.getSemanticType()));
		table.addSymbolToCurScope(routineDecl.getName(), this.openRoutines.peek());
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

	private void checkArgumentTypeMatch(IRoutineCall routineCall, SymbolTable symbolTable) throws SemanticErrorException {
		ASTList<Expn> args = routineCall.getArguments();

		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(symbolTable, routineCall.getIdentifier());
		ArrayList<PrimitiveSemType> params = routineSemType.getParameters();

		checkArgumentCount(routineCall, symbolTable);
		for (int i = 0; i < args.size(); i++) {
			PrimitiveSemType argType = args.get(i).getResultType();
			if (!argType.equals(params.get(i))) {
				throw new SemanticErrorException("In '" + routineCall.getIdentifier() + "', parameter " + i + " is expected to be a "
						+ params.get(i) + " but a " + argType + " was provided instead");
			}
		}
	}

	private void checkArgumentCount(IRoutineCall routineCall, SymbolTable symbolTable) throws SemanticErrorException {
		RoutineSemType routine = getRoutineSemTypeFromSymbolTable(symbolTable, routineCall.getIdentifier());

		int numArgsInCall = routineCall.getArguments().size();
		int numParamsInDec = routine.getNumParameters();

		if (numArgsInCall != numParamsInDec) {
			throw new InvalidNumberOfArgumentsException(routineCall.getIdentifier(), numParamsInDec, numArgsInCall);
		}
	}

	private void checkForNoParameters(IIdentifier routineCall, SymbolTable symbolTable) throws SemanticErrorException {
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(symbolTable, routineCall.getIdentifier());

		int numParams = routineSemType.getNumParameters();
		if (numParams != 0) {
			throw new InvalidNumberOfArgumentsException(routineCall.getIdentifier(), numParams, 0);
		}
	}

	private void checkIdentifierIsFunction(IIdentifier funCallExpn, SymbolTable symbolTable) throws SemanticErrorException {
		String ident = funCallExpn.getIdentifier();
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(symbolTable, ident);

		// Make sure it is a function and not a procedure. Functions should have a return type.
		if (routineSemType.getReturnType() == null) {
			throw new SemanticErrorException("'" + ident + "' is a procedure, but a function was exptected.");
		}
	}

	private void checkIdentifierIsProcedure(ProcedureCallStmt procCallStmt, SymbolTable symbolTable) throws SemanticErrorException {
		String ident = procCallStmt.getIdentifier();
		RoutineSemType routineSemType = getRoutineSemTypeFromSymbolTable(symbolTable, ident);

		// Make sure it is a procedure and not a function. Procedures should NOT have a return type.
		if (routineSemType.getReturnType() != null) {
			throw new SemanticErrorException("'" + ident + "' is a function, not a procedure was exptected.");
		}
	}

	private void checkRoutineHasReturnStatement() throws SemanticErrorException {
		if (!this.openRoutines.peek().seenReturnStatement()) {
			throw new SemanticErrorException("Function or procedure is missing a return statement.");
		}
	}

	private void checkReturnIsInRoutine() throws SemanticErrorException {
		if (this.openRoutines.isEmpty()) {
			throw new SemanticErrorException("Call to return outside of a procedure or function.");
		}
	}

	private void checkIdentifierIsScalar(IVisitableElement element) throws SemanticErrorException {
		IdentExpn ident = (IdentExpn) element;
		Symbol symbol = this.table.retrieveSymbol(ident.getIdentifier());
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident.getIdentifier());
		}
	}

	private void checkIdentiferIsArray(SubsExpn element) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(element.getVariable());
		if (symbol == null) {
			throw new UndeclaredSymbolException(element.getVariable());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof ArraySemType)) {
			throw new NotArrayException(element.getVariable());
		}
		
		ArraySemType array = (ArraySemType)symbol.getType();
		if (array.getDimensions() != element.getDimensions()) {
			throw new SemanticErrorException("Array expects " + array.getDimensions() + " dimensions but is indexed by " + element.getDimensions() + " dimensions.");
		}
	}

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

	private void checkReturnType(ReturnStmt returnStmt) throws SemanticErrorException {
		checkReturnIsInRoutine();
		if (!this.openRoutines.peek().getReturnType().equals(returnStmt.getValue().getResultType())) {
			throw new SemanticErrorException("Trying to return value of type " + returnStmt.getValue().getResultType() + " when type "
					+ this.openRoutines.peek().getReturnType() + " is required.");
		}
	}

	private void checkAssignmentTypesMatch(AssignStmt assignStmt) throws SemanticErrorException {
		if (assignStmt.getRval().getResultType() == null || assignStmt.getLval().getResultType() == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}
		if (!assignStmt.getLval().getResultType().equals(assignStmt.getRval().getResultType())) {
			throw new SemanticErrorException("Trying to assign a value of type " + assignStmt.getRval().getResultType()
					+ " to a variable of type " + assignStmt.getLval().getResultType());
		}
	}

	private void checkExpnType(Expn expression, SemType resultType) throws SemanticErrorException {
		PrimitiveSemType expResultType = expression.getResultType();
		if (expResultType == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}
		else if (expression.hasError()) {
			//Don't do anything. The user should already have seen a message about this error.
		}
		else if ( !expResultType.equals(resultType) ) {
			expression.setResultType(PrimitiveSemType.ERROR);
			throw new SemanticErrorException("Expected a " + resultType + " and found a " + expResultType + ".");
		}
	}

	private void checkBinaryExpnTypesMatch(BinaryExpn expression) throws SemanticErrorException {
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

	private void setExpressionResultTypeFromIdentifier(IdentExpn ident) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(ident.getIdentifier());
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident.getIdentifier());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof PrimitiveSemType)) {
			throw new SemanticErrorException("Trying to use non-primitive identifier in an expression.");
		}

		setExpressionResultType(ident, (PrimitiveSemType) symbol.getType());
	}

	private void setExpressionResultTypeFromArray(SubsExpn ident) throws SemanticErrorException {
		checkIdentiferIsArray(ident);
		Symbol symbol = this.table.retrieveSymbol(ident.getVariable());
		ArraySemType array = (ArraySemType) symbol.getType();
		setExpressionResultType(ident, array.getBaseType());
	}
	
	private void setExpressionResultTypeFromFunction(IIdentifier identifier) throws SemanticErrorException {
		checkIdentifierIsFunction(identifier, table);
		Symbol symbol = this.table.retrieveSymbol(identifier.getIdentifier());
		RoutineSemType routine = (RoutineSemType)symbol.getType();
		setExpressionResultType(identifier, routine.getReturnType());
	}

	private void setExpressionResultType(IVisitableElement element, PrimitiveSemType resultType) {
		Expn ast = (Expn) element;
		ast.setResultType(resultType);
	}
}
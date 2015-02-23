package compiler488.semantics;

import compiler488.ast.BaseAST;
import compiler488.ast.BaseAST.ResultType;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.expn.BinaryExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.type.Type;
import compiler488.symbol.SymbolTable;
import compiler488.utilities.IVisitableElement;

public class SemanticActions {

	public static void checkSemanticRule(SymbolTable table, int action, IVisitableElement element) throws SemanticErrorException {
		switch (action) {

		// Scopes and Program

		case 0: // Open program scope.
		case 4: // Open function scope.
		case 8: // Open procedure scope.
			table.openMajorScope();
			break;

		case 1: // End all scopes.
		case 5:
		case 7:
		case 9:
			table.closeCurrentScope();
			break;

		case 3: // Associate statements and declarations with scope.
			// Do nothing, this happens automatically.
			break;

		case 6: // Open ordinary scope.
			table.closeCurrentScope();
			break;

		// Declarations

		case 10: // Declare scalar.
			// According to our CUP file, all scalar declarations are
			// multi-declarations.
			MultiDeclarations allDeclarations = (MultiDeclarations) element;
			for (DeclarationPart part : allDeclarations.getParts()) {
				table.addSymbolToCurScope(part.getName(), allDeclarations.getType());
			}
			break;

		// Expressions

		case 20: // Set result type to boolean.
			setExpressionResultType(element, Type.BOOLEAN);
			break;

		case 21: // Set result type to integer.
			setExpressionResultType(element, Type.INTEGER);
			break;

		case 23: // Set result type to type of expression
			// This should happen automatically.
			break;
		case 24: // Set result type to type of anonymous function
			printTodo(); // TODO
			break;
		case 25: // Set result type to type of parametername.
			// setExpressionResultType(element, ResultType.PARAMETER_NAME);
			break;
		case 26: // Set result type to type of variablename.
			// setExpressionResultType(element, ResultType.VARIABLE_NAME);
			break;
		case 27: // Set result type to type of array element.
			// setExpressionResultType(element, ResultType.ARRAY_ELEMENT);
			break;
		case 28: // Set result type to result type of function.
			printTodo(); // TODO
			break;

		case 30: // Check that type of expression is boolean.
			checkExpnType((Expn) element, Type.BOOLEAN);
			break;
		case 31: // Check that type of expression or variable is integer
			checkExpnType((Expn) element, Type.INTEGER);
			break;
		case 32: // Check that left and right operand expressions are the same type
			checkBinaryExpnTypesMatch((BinaryExpn) element);
			break;
		case 33: // This one should be missing.
			break;
		case 34: // Check that variable and expression in assignment are the same type.
			checkAssignmentTypesMatch((AssignStmt) element);
			break;
		case 35: // Check that expression type matches the return type of enclosing function.
			checkReturnType((ReturnStmt) element);
			break;
		case 36: // Check that type of argument expression matches type of
			// corresponding formal parameter.
			checkArgumentTypeMatch(element);
		case 37: // Check that identifier has been declared as a scalar
			// variable.
			checkVariableDeclaration(element);
		case 38: // Check that identifier has been declared as an array.
		case 39: // Check that identifier has been declared as a parameter.

		case 47: // associate type with variables
			// Do nothing, this happens automatically.
			break;

		default:
			printTodo();
			break;
		}
	}

	private static void printTodo() {
		System.out.println("TODO: Semantic action not implemented!");
	}

	private static void checkVariableDeclaration(IVisitableElement element) throws SemanticErrorException {
		throw new UnsupportedOperationException("TODO.");
	}

	private static void checkArgumentTypeMatch(IVisitableElement element) throws SemanticErrorException {
		throw new UnsupportedOperationException("TODO.");
	}

	private static void checkReturnType(ReturnStmt returnStmt) throws SemanticErrorException {
		if ( != returnStmt.getValue().getResultType()) {
			throw new SemanticErrorException("Function returns a " + returnStmt.getValue().getResultType() + 
											  " but a " + returnStmt.getResultType() + "was expected.");
		}
	}

	private static void checkAssignmentTypesMatch(AssignStmt assignStmt) throws SemanticErrorException {
		if (!assignStmt.getLval().getResultType().equals(assignStmt.getRval().getResultType())) {
			throw new SemanticErrorException("Trying to assign a value of type " + assignStmt.getRval().getResultType() + "to a variable of type " + assignStmt.getLval().getResultType());
		}
	}

	private static void checkExpnType(Expn expression, Type resultType) throws SemanticErrorException {
		if (expression.getResultType() == null) {
			throw new Exception("The result type of the expression has not been set! There is an ordering problem.");
		}
		if (!expression.getResultType().equals(resultType)) {
			throw new SemanticErrorException("Expected a " + resultType + " and found a " + expression.getResultType());
		}
	}

	private static void checkBinaryExpnTypesMatch(BinaryExpn expression) throws SemanticErrorException {
		Type firstResultType = expression.getFirstExpression().getResultType();
		Type secondResultType = expression.getSecondExpression().getResultType();

		boolean resultIsGood = (firstResultType.equals(secondResultType));
		if (!resultIsGood) {
			throw new SemanticErrorException("The left side of the expression resolves to a " + firstResultType + "while the right resolves to a " + secondResultType + ".");
		}
	}

	private static void setExpressionResultType(IVisitableElement element, Type resultType) {
		Expn ast = (Expn) element;
		ast.setResultType(resultType);
	}
}
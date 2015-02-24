package compiler488.semantics;

import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.BinaryExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.type.Type;
import compiler488.symbol.ArraySemType;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import compiler488.utilities.IVisitableElement;

public class SemanticActions {

	public static final String NULL_RESULT_TYPE_EXCEPTION = "Cannot evaluate expression. (COULD INDICATE MISSING RESULT TYPE SET)";

	private Semantics semantics;
	private SymbolTable table;

	public SemanticActions(Semantics semantics, SymbolTable table) {
		this.table = table;
		this.semantics = semantics;
	}

	public void checkSemanticRule(int action, IVisitableElement element) throws SemanticErrorException {
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
			table.addSymbolToCurScope(((ScalarDeclPart) element).getName(), this.semantics.getCurrentDeclarationType());
			break;

		case 15: // Declare parameter.
			ScalarDecl scalarDecl = (ScalarDecl) element;
			table.addSymbolToCurScope(scalarDecl.getName(), scalarDecl.getType().getSemanticType());
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
			printTodo(); // TODO
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
			checkArgumentTypeMatch(element);
			break;

		case 37: // Check that identifier has been declared as a scalar
			// variable.
		case 39: // Check that identifier has been declared as a parameter.
			checkVariableDeclaration(element);
			break;

		case 38: // Check that identifier has been declared as an array.
			checkArrayDeclaration((SubsExpn)element);
			break;

		default:
			printTodo();
			break;
		}
	}

	private void printTodo() {
		System.out.println("TODO: Semantic action not implemented!");
	}

	private void checkVariableDeclaration(IVisitableElement element) throws SemanticErrorException {
		IdentExpn ident = (IdentExpn) element;
		Symbol symbol = this.table.retrieveSymbol(ident.getIdent());
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident.getIdent());
		}
	}
	
	private void checkArrayDeclaration(SubsExpn element) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(element.getVariable());
		if (symbol == null) {
			throw new UndeclaredSymbolException(element.getVariable());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof ArraySemType)) {
			throw new NotArrayException(element.getVariable());
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

	private void checkArgumentTypeMatch(IVisitableElement element) throws SemanticErrorException {
		throw new UnsupportedOperationException("TODO.");
	}

	private void checkReturnType(ReturnStmt returnStmt) throws SemanticErrorException {
		throw new UnsupportedOperationException("TODO.");
		/*
		 * if ( != returnStmt.getValue().getResultType()) { throw new SemanticErrorException("Function returns a " + returnStmt.getValue().getResultType() + " but a " + returnStmt.getResultType() +
		 * "was expected."); }
		 */
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
		if (expression.getResultType() == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}
		if (!expression.getResultType().equals(resultType)) {
			throw new SemanticErrorException("Expected a " + resultType + " and found a " + expression.getResultType() + ".");
		}
	}

	private void checkBinaryExpnTypesMatch(BinaryExpn expression) throws SemanticErrorException {
		SemType firstResultType = expression.getFirstExpression().getResultType();
		SemType secondResultType = expression.getSecondExpression().getResultType();

		if (firstResultType == null || secondResultType == null) {
			throw new SemanticErrorException(NULL_RESULT_TYPE_EXCEPTION);
		}

		boolean resultIsGood = (firstResultType.equals(secondResultType));
		if (!resultIsGood) {
			throw new SemanticErrorException("The left side of the expression resolves to a " + firstResultType
					+ "while the right resolves to a " + secondResultType + ".");
		}
	}

	private void setExpressionResultTypeFromIdentifier(IdentExpn ident) throws SemanticErrorException {
		Symbol symbol = this.table.retrieveSymbol(ident.getIdent());
		if (symbol == null) {
			throw new UndeclaredSymbolException(ident.getIdent());
		}

		// HACK: Can we avoid instanceof?
		if (!(symbol.getType() instanceof PrimitiveSemType)) {
			throw new SemanticErrorException("Trying to use non-primitive identifier in an expression.");
		}

		setExpressionResultType(ident, (PrimitiveSemType) symbol.getType());
	}
	
	private void setExpressionResultTypeFromArray(SubsExpn ident) throws SemanticErrorException {
		checkArrayDeclaration(ident);
		Symbol symbol = this.table.retrieveSymbol(ident.getVariable());
		ArraySemType array = (ArraySemType)symbol.getType();
		setExpressionResultType(ident, array.getBaseType());
	}

	private void setExpressionResultType(IVisitableElement element, PrimitiveSemType resultType) {
		Expn ast = (Expn) element;
		ast.setResultType(resultType);
	}
}
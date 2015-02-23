package compiler488.semantics;

import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.symbol.*;
import compiler488.utilities.IVisitableElement;

public class SemanticActions {

	public static void checkSemanticRule(SymbolTable table, int action, IVisitableElement element) throws SemanticErrorException {
		switch (action) {

		// Scopes and Program

		case 0: // program scope
		case 4: // function scope
		case 8: // procedure scope
			table.openMajorScope();
			break;

		case 1: // end all scopes
		case 5:
		case 7:
		case 9:
			table.closeCurrentScope();
			break;

		case 3: // associate statements and declarations with scope
			// do nothing, this happens automatically
			break;

		case 6: // ordinary scope
			table.closeCurrentScope();
			break;

		// Declarations

		case 10: // declare scalar
			// according to our CUP file, all scalar declarations are
			// multi-declarations
			MultiDeclarations allDeclarations = (MultiDeclarations) element;
			for (DeclarationPart part : allDeclarations.getParts()) {
				table.addSymbolToCurScope(part.getName(), allDeclarations.getType());
			}
			break;

		case 47: // associate type with variables
			// do nothing, this happens automatically
			break;

		default:

			System.out.println("TODO: Semantic action not implemented!");
			break;
		}
	}
}
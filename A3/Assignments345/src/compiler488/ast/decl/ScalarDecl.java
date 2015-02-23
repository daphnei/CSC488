package compiler488.ast.decl;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;
import compiler488.utilities.IVisitor;

/**
 * Represents the declaration of a simple variable.
 * NOTE: In the CUP file, we only use this type for parameter declarations.
 */
public class ScalarDecl extends Declaration {
	public ScalarDecl(String name, Type type, int left, int right) {
		super(name, type, left, right);
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(type + " " + name);
	}
	
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.utilities.IVisitor;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
	
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

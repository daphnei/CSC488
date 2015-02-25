package compiler488.ast.expn;

import compiler488.utilities.IVisitor;


/**
 * This is a place holder for literal constants.
 */
public abstract class ConstExpn extends Expn {
	
	public ConstExpn(int left, int right) {
		super(left, right);
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

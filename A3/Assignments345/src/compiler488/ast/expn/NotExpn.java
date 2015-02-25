package compiler488.ast.expn;

import compiler488.utilities.IVisitor;

/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(Expn operand,int left, int right) {
        super(UnaryExpn.OP_NOT, operand, left,right);
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

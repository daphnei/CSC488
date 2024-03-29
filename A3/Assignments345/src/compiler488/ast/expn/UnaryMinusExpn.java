package compiler488.ast.expn;

import compiler488.interfaces.IVisitor;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand, int left, int right) {
        super(UnaryExpn.OP_MINUS, operand,left,right);
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

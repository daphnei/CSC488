package compiler488.ast.expn;

import compiler488.interfaces.IVisitor;


/**
 * Boolean literal constants.
 */
public class BoolConstExpn extends ConstExpn {
    /** The value of the constant */
    private boolean value;

    public BoolConstExpn(boolean value, int left, int right) {
    	super(left, right);

        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString () {
        return value ? "true" : "false";
    }
    
	@Override
	public void accept(IVisitor visitor) {		
		visitor.visit(this);	
	}
}

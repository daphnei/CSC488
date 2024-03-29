package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.interfaces.IVisitor;
import compiler488.semantics.types.IntegerSemType;

/**
 * Represents a literal text constant.
 */
public class TextConstExpn extends ConstExpn implements Printable {
    /** The value of this literal. */
    private String value;
    
    public TextConstExpn(String value, int left, int right) {
    	super(left, right);

        this.value = value;
        
        this.setResultType(IntegerSemType.INTEGER);
    }

    public String getValue() {
        return value;
    }
    
    /**
     * Returns a description of the literal text constant.
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

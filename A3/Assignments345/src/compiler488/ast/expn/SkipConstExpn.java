package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.interfaces.IVisitor;
import compiler488.semantics.types.IntegerSemType;

/**
 * Represents the special literal constant associated with writing a new-line
 * character on the output device.
 */
public class SkipConstExpn extends ConstExpn implements Printable {
    public SkipConstExpn(int left, int right) {
    	super(left, right);
        this.setResultType(IntegerSemType.INTEGER);
    }

    @Override
    public String toString() {
        return "skip";
    }

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}    
}

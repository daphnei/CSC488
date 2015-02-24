package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.symbol.SemType;
import compiler488.utilities.IVisitor;

/**
 * Represents the special literal constant associated with writing a new-line
 * character on the output device.
 */
public class SkipConstExpn extends ConstExpn implements Printable {
    public SkipConstExpn() {
        super();
        this.setResultType(SemType.INTEGER);
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

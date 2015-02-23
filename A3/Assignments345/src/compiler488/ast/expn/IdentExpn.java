package compiler488.ast.expn;

import compiler488.ast.BasePrettyPrinter;
import compiler488.ast.Readable;
import compiler488.utilities.IVisitor;

/**
 *  References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends Expn implements Readable {
    /** Name of the identifier. */
    private String ident;

    public IdentExpn(String ident, int left, int right) {
        super();
        this.ident = ident;
        this.left = left;
        this.right = right;
    }

    public String getIdent() {
        return ident;
    }
    
    /**
     * Returns the name of the variable or function.
     */
    @Override
    public String toString() {
        return ident;
    }

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

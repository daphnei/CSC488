package compiler488.ast.expn;

import compiler488.ast.Readable;

/**
 *  References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends Expn implements Readable {
    /** Name of the identifier. */
    private String ident;
    private int left;
    private int right;

    public IdentExpn(String ident, int left, int right) {
        super();

        this.ident = ident;
        this.left = left;
        this.right = right;
    }

    public String getIdent() {
        return ident;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    /**
     * Returns the name of the variable or function.
     */
    @Override
    public String toString() {
        return ident;
    }

}

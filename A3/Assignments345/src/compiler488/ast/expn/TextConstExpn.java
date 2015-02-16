package compiler488.ast.expn;

import compiler488.ast.Printable;

/**
 * Represents a literal text constant.
 */
public class TextConstExpn extends ConstExpn implements Printable {
    /** The value of this literal. */
    private String value;
    
    private int left;
    private int right;

    public TextConstExpn(String value, int left, int right) {
        super();

        this.value = value;
        this.left = left;
        this.right = right;
    }

    public String getValue() {
        return value;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    /**
     * Returns a description of the literal text constant.
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

}

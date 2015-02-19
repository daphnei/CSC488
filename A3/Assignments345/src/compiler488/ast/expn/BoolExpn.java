package compiler488.ast.expn;



/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR 	= "|";
    public final static String OP_AND	= "&";

    public BoolExpn(String opSymbol, Expn first, Expn second, int left, int right) {
        super(opSymbol, first, second, left, right);

        assert ((opSymbol == OP_OR) ||
                (opSymbol == OP_AND));
    }
}

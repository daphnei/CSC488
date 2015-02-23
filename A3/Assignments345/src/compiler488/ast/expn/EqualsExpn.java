package compiler488.ast.expn;

import compiler488.semantics.SemanticErrorException;
import compiler488.utilities.IVisitor;



/**
 * Place holder for all binary expression where both operands could be either
 * integer or boolean expressions. e.g. = and != comparisons
 */
public class EqualsExpn extends BinaryExpn {
    public final static String OP_EQUAL 	= "=";
    public final static String OP_NOT_EQUAL	= "!=";

    public EqualsExpn(String opSymbol, Expn first, Expn second, int left, int right) {
        super(opSymbol, first, second,left, right);

        assert ((opSymbol == OP_EQUAL) ||
                (opSymbol == OP_NOT_EQUAL));
    }
    
	@Override
	public void accept(IVisitor visitor) {		
		visitor.visit(this);	
	}

}

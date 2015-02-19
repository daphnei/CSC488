package compiler488.ast.expn;

import compiler488.ast.PrettyPrinter;
import compiler488.utilities.IVisitor;

/**
 * The common features of unary expressions.
 */
public abstract class UnaryExpn extends Expn {
    public final static String OP_NOT	= "!";
    public final static String OP_MINUS	= "-";

    /** Operand of the unary operator. */
    private Expn operand;
    
    /**
     * The symbol of the operator.
     *
     * <p>Must be one of <code>UnaryExpn.OP_NOT</code> or
     * <code>UnaryExpn.OP_MINUS</code>.</p>
     */
    private String opSymbol;

    public UnaryExpn(String opSymbol, Expn operand,int left, int right) {
        super();

        this.opSymbol = opSymbol;
        this.operand = operand;
        this.left = left;
        this.right = right;

        assert ((opSymbol == OP_NOT) ||
                (opSymbol == OP_MINUS));
    }

    public Expn getOperand() {
        return operand;
    }

    public String getOpSymbol() {
        return opSymbol;
    }
    
    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(opSymbol + "(");
        operand.prettyPrint(p);
        p.print(")");

    }
    
	@Override
	public void accept(IVisitor visitor) {
		this.operand.accept(visitor);
		
		visitor.visit(this);	
	}

}

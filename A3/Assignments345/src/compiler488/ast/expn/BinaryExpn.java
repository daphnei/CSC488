package compiler488.ast.expn;

import compiler488.ast.PrettyPrinter;
import compiler488.utilities.IVisitor;

/**
 * The common features of binary expressions.
 */
public abstract class BinaryExpn extends Expn {
    /** Left operand of the binary operator. */
    protected Expn first;

    /** Right operand of the binary operator. */
    protected Expn second;
    
    /** Symbol of the operator.
     *
     * <p>Must be a <code>static final</code> constant defined in a subclass of
     * BinaryExpn.</p>
     */
    protected String opSymbol;

    protected BinaryExpn(String opSymbol, Expn first, Expn second, int left, int right) {
        super();
        
        this.opSymbol = opSymbol;
        this.first = first;
        this.second = second;
        this.left = left;
        this.right = right;
    }

    public String getOpSymbol() {
        return opSymbol;
    }
    
    public Expn getFirstExpression(){
        return first;
    }
    
    public Expn getSecondExpression(){
        return second;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("(");
        first.prettyPrint(p);
        p.print(") " + opSymbol + " (");
        second.prettyPrint(p);
        p.print(")");
    }
    
	@Override
	public void accept(IVisitor visitor) {		
		visitor.visit(this);	
	}
}

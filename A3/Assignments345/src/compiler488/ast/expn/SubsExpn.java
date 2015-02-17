package compiler488.ast.expn;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.Readable;
import compiler488.utilities.IVisitor;

/**
 * References to an array element variable
 */
public class SubsExpn extends Expn implements Readable {
    /** Name of the array variable. */
    private String variable;

    /** First subscript. */
    private Expn subscript1;
    private int left;
    private int right;

    /** Second subscript (if any.) */
    private Expn subscript2 = null;

    public SubsExpn(String variable, Expn subscript1, Expn subscript2,int left, int right) {
        super();

        this.variable = variable;
        this.subscript1 = subscript1;
        this.subscript2 = subscript2;
        this.left = left;
        this.right = right;
    }

    public SubsExpn(String variable, Expn subscript1,int left, int right) {
        this(variable, subscript1, null,left,right);
    }

    public String getVariable() {
        return variable;
    }
    
    public int getLeft(){
        return left;
    }
    
    public int getRight(){
        return right;
    }

    public Expn getSubscript1() {
        return subscript1 ;
    }

    public Expn getSubscript2() {
        return subscript2;
    }

    public int numSubscripts() {
        return 1 + (subscript2 != null ? 1 : 0);
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print(variable + "[");

        subscript1.prettyPrint(p);

        if (subscript2 != null) {
            p.print(", ");
            subscript2.prettyPrint(p);
        }

        p.print("]");
    }

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

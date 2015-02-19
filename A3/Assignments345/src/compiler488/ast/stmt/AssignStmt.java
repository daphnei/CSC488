package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.utilities.IVisitor;

/**
 * Holds the assignment of an expression to a variable.
 */
public class AssignStmt extends Stmt {
    /** The location being assigned to. */
    private Expn lval;

    /** The value being assigned. */
    private Expn rval;

    public AssignStmt(Expn lval, Expn rval, int left, int right) {
        super();

        this.lval = lval;
        this.rval = rval;
        this.left = left;
        this.right = right;
    }

    public Expn getLval() {
        return lval;
    }

    public Expn getRval() {
        return rval;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        lval.prettyPrint(p);
        p.print(" <= ");
        rval.prettyPrint(p);
    }

    @Override
	public void accept(IVisitor visitor) {
    	visitor.visit(this.lval);
    	visitor.visit(this.rval);
		visitor.visit(this);	
	}
}

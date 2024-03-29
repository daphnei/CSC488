package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.*;
import compiler488.interfaces.IVisitor;

/**
 * Represents the command to exit from a loop.
 */
public class ExitStmt extends Stmt {
    /** Condition expression for <code>exit when</code> variation. */
    private Expn expn = null;

    public ExitStmt(Expn expn, int left, int right) {
    	super(left, right);

        this.expn = expn;
    }

    public ExitStmt(int left, int right) {
        this(null,left, right);
    }

    public Expn getExpn() {
        return expn;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("exit");

        if (expn != null) {
            p.print(" when ");
            expn.prettyPrint(p);
        }
    }
    
    @Override
	public void accept(IVisitor visitor) {    	
		visitor.visit(this);	
	}

}

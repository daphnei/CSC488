package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.*;

/**
 * Represents the command to exit from a loop.
 */
public class ExitStmt extends Stmt {
    /** Condition expression for <code>exit when</code> variation. */
    private Expn expn = null;
    
    private int left;
    private int right;

    public ExitStmt(Expn expn, int left, int right) {
        super();

        this.expn = expn;
        this.left = left;
        this.right = right;
    }

    public ExitStmt(int left, int right) {
        this(null,left, right);
    }

    public Expn getExpn() {
        return expn;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("exit");

        if (expn != null) {
            p.print(" when ");
            expn.prettyPrint(p);
        }
    }

}

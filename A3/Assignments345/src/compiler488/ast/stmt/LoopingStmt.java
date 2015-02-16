package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
    /** The control expression for the looping construct (if any.) */
    protected Expn expn = null;

    private int left;
    private int right;
    /** The body of the looping construct. */
    protected ASTList<Stmt> body;

    public LoopingStmt(Expn expn, ASTList<Stmt> body, int left, int right) {
        super();

        this.expn = expn;
        this.body = body;
        this.left = left;
        this.right = right;
    }

    public LoopingStmt(ASTList<Stmt> body, int left, int right) {
        this(null, body, left, right);
    }

    public Expn getExpn() {
        return expn;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

}

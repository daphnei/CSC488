package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;
import compiler488.utilities.IVisitor;

/**
 * Represents the common parts of loops.This is abstract, ie, it is never
 * used directly, only extended.
 */
public abstract class LoopingStmt extends Stmt {
    /** The control expression for the looping construct (if any.) */
    protected Expn expn = null;

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

    @Override
	public void accept(IVisitor visitor) {
    	//TODO: I am not sure I have this one right.
    	
    	this.expn.accept(visitor);
    	
    	for (Stmt bodyStmt : this.body) {
    		bodyStmt.accept(visitor);
    	}
    	
		visitor.visit(this);	
	}
}

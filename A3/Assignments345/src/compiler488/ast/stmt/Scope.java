package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.utilities.IVisitor;

/**
 * Represents the declarations and instructions of a scope construct.
 */
public class Scope extends Stmt {
    /** Body of the scope, mixed list of declarations and statements. */
    protected ASTList<Stmt> body;

    public Scope(int left, int right) {
    	super(left, right);

        this.body = null;
    }

    public Scope(ASTList<Stmt> body, int left, int right) {
        this(left, right);

        this.body = body;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.println("begin");
        if (body != null && body.size() > 0) {
            body.prettyPrintBlock(p);
        }
        p.print("end");
    }

    @Override
	public void accept(IVisitor visitor) {   	
		visitor.visit(this);	
	}
}

package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;

/**
 * Represents the declarations and instructions of a scope construct.
 */
public class Scope extends Stmt {
    /** Body of the scope, mixed list of declarations and statements. */
    protected ASTList<Stmt> body;
    
    private int left;
    private int right;

    public Scope(int left, int right) {
        super();

        this.body = null;
    }

    public Scope(ASTList<Stmt> body, int left, int right) {
        this(left, right);

        this.body = body;
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

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.println("begin");
        if (body != null && body.size() > 0) {
            body.prettyPrintBlock(p);
        }
        p.print("end");
    }

}

package compiler488.ast.expn;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.stmt.Stmt;

/**
 * Represents the parameters and instructions associated with a function or
 * procedure.
 */
public class AnonFuncExpn extends Expn {
    /** Execute these statements. */
    private ASTList<Stmt> body;

    /** The expression whose value is yielded. */
    private Expn expn;
    
    private int left;
    private int right;

    public AnonFuncExpn(ASTList<Stmt> body, Expn expn, int left, int right) {
        super();

        this.body = body;
        this.expn = expn;
        this.left = left;
        this.right = right;
    }

    public ASTList<Stmt> getBody() {
        return body;
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

    public void prettyPrint(PrettyPrinter p) {
        p.println("{");
        p.enterBlock();

        body.prettyPrintNewlines(p);

        p.print("yields ");
        expn.prettyPrint(p);
        p.newline();

        p.exitBlock();
        p.println("}");
    }

}

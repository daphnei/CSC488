package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;

/**
 * Represents an if-then or an if-then-else construct.
 */
public class IfStmt extends Stmt {
    /** The condition that determines which branch to execute. */
    private Expn condition;

    /** Represents the statement to execute when the condition is true. */
    private ASTList<Stmt> whenTrue;
    
    private int left;
    private int right;

    /** Represents the statement to execute when the condition is false. */
    private ASTList<Stmt> whenFalse = null;

    public IfStmt(Expn condition, ASTList<Stmt> whenTrue, ASTList<Stmt> whenFalse, int left, int right) {
        super();

        this.condition = condition;
        this.whenTrue = whenTrue;
        this.whenFalse = whenFalse;
        this.left = left;
        this.right = right;
    }

    public IfStmt(Expn condition, ASTList<Stmt> whenTrue, int left, int right) {
        this(condition, whenTrue, null,left, right);
    }

    public Expn getCondition() {
        return condition;
    }

    public ASTList<Stmt> getWhenTrue() {
        return whenTrue;
    }

    public ASTList<Stmt> getWhenFalse() {
        return whenFalse;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    /**
     * Print a description of the <strong>if-then-else</strong> construct. If the
     * <strong>else</strong> part is empty, just print an <strong>if-then</strong> construct.
     */
    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("if ");
        condition.prettyPrint(p);
        p.println(" then");
        whenTrue.prettyPrintBlock(p);

        if (whenFalse != null) {
            p.println(" else");
            whenFalse.prettyPrintBlock(p);
        }

        p.println("end");
    }

}
package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.interfaces.IVisitor;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class LoopStmt extends LoopingStmt {
    public LoopStmt(ASTList<Stmt> body, int left, int right) {
        super(body, left, right);
    }

    /**
     * Pretty-print this AST node as a <code>loop</code> loop.
     */
    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.println("loop");
        body.prettyPrintNewlines(p);
        p.println("end");
    }
    
    @Override
	public void accept(IVisitor visitor) {	
		visitor.visit(this);	
	}
}

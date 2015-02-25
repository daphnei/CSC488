package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.interfaces.IVisitor;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class WhileDoStmt extends LoopingStmt {
    public WhileDoStmt(Expn expn, ASTList<Stmt> body, int left, int right) {
        super(expn, body, left, right);
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("while ");
        expn.prettyPrint(p);
        p.println(" do");

        body.prettyPrintBlock(p);

        p.println("end");
    }
    
    @Override
	public void accept(IVisitor visitor) {	
		visitor.visit(this);	
	}
}

package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.interfaces.IVisitor;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {
    public Program(ASTList<Stmt> body, int left, int right) {
        super(body,left, right);
    }

    public Program(Scope scope,int left, int right) {
        super(scope.getBody(), left, right);
    }
    
    @Override
	public void accept(IVisitor visitor) {   	
		visitor.visit(this);	
	}
}

package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.interfaces.IVisitor;

/**
 * The command to read data into one or more variables.
 */
public class GetStmt extends Stmt {
    /** A list of locations to store the values read. */
    private ASTList<Expn> inputs;
    
    public GetStmt (ASTList<Expn> inputs, int left, int right) {
    	super(left, right);

        this.inputs = inputs;
    }

    public ASTList<Expn> getInputs() {
        return inputs;
    }

    @Override
	public void prettyPrint(PrettyPrinter p) {
        p.print("get ");
        inputs.prettyPrintCommas(p);
    }

    @Override
	public void accept(IVisitor visitor) {    	
		visitor.visit(this);	
	}
}

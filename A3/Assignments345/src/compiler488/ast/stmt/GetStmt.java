package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.utilities.IVisitor;

/**
 * The command to read data into one or more variables.
 */
public class GetStmt extends Stmt {
    /** A list of locations to store the values read. */
    private ASTList<Expn> inputs;
    
    private int left;
    private int right;

    public GetStmt (ASTList<Expn> inputs, int left, int right) {
        super();

        this.inputs = inputs;
        this.left = left;
        this.right = right;
    }

    public ASTList<Expn> getInputs() {
        return inputs;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print("get ");
        inputs.prettyPrintCommas(p);
    }

    @Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

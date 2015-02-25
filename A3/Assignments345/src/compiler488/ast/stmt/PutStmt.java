package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.Printable;
import compiler488.utilities.IVisitor;

/**
 * The command to write data on the output device.
 */
public class PutStmt extends Stmt {
    /** The values to be printed. */
    private ASTList<Printable> outputs;

    public PutStmt(ASTList<Printable> outputs, int left, int right) {
    	super(left, right);

        this.outputs = outputs;
    }

    public ASTList<Printable> getOutputs() {
        return outputs;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("put ");
        outputs.prettyPrintCommas(p);
    }

    @Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.interfaces.IRoutineCall;
import compiler488.interfaces.IVisitor;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt implements IRoutineCall {
    /** The name of the procedure being called. */
    private String name;

    /** The arguments passed to the procedure (if any.)
     *
     * <p>This value must be non-<code>null</code>. If the procedure takes no
     * parameters, represent that with an empty list here instead.</p>
     */
    private ASTList<Expn> arguments;

    public ProcedureCallStmt(String name, ASTList<Expn> arguments,int left, int right) {
    	super(left, right);

        this.name = name;
        this.arguments = arguments;
    }

    public ProcedureCallStmt(String name, int left, int right) {
        this(name, new ASTList<Expn>(), left, right);
    }

    @Override
    public ASTList<Expn> getArguments() {
        return arguments;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(name);

        if ((arguments != null) && (arguments.size() > 0)) {
            p.print("(");
            arguments.prettyPrintCommas(p);
            p.print(")");
        }
    }

    @Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}

	@Override
	public String getIdentifier() {
		return this.name;
	}
}

package compiler488.ast.decl;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.stmt.Scope;
import compiler488.ast.type.Type;
import compiler488.utilities.IVisitor;

/**
 * Represents the declaration of a function or procedure.
 */
public class RoutineDecl extends Declaration {
    /** The formal parameters for this routine (if any.)
     *
     * <p>This value must be non-<code>null</code>. If absent, use an empty
     * list instead.</p>
     */
    private ASTList<ScalarDecl> parameters =  new ASTList<ScalarDecl>();

    /** The body of this routine (if any.) */
    private Scope body = null;

    /**
     * Construct a function with parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  type	      Type returned by the function
     *   @param  parameters   List of parameters to the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(String name, Type type, ASTList<ScalarDecl> parameters, Scope body, int left, int right) {
        super(name, type, left , right);

        this.parameters = parameters;
        this.body = body;
    }

    /**
     * Construct a function with no parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  type	      Type returned by the function
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(String name, Type type, Scope body, int left, int right) {
        this(name, type, new ASTList<ScalarDecl>(), body, left, right);
    }

    /**
     * Construct a procedure with parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  parameters   List of parameters to the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(String name, ASTList<ScalarDecl> parameters, Scope body, int left, int right) {
        this(name, null, parameters, body, left , right);

        this.parameters = parameters;
        this.body = body;
    }

    /**
     * Construct a procedure with no parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(String name, Scope body, int left, int right) {
        this(name, null, new ASTList<ScalarDecl>(), body, left, right);
    }

    public ASTList<ScalarDecl> getParameters() {
        return parameters;
    }

    public Scope getBody() {
        return body;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        if (type == null) {
            p.print("procedure ");
        } else {
            type.prettyPrint(p);
            p.print(" function ");
        }

        p.print(name);

        if (parameters.size() > 0) {
            p.print("(");
            parameters.prettyPrintCommas(p);
            p.print(")");
        }

        if (body != null) {
            p.print(" ");
            body.prettyPrint(p);
        }
    }
    
	@Override
	public void accept(IVisitor visitor) {
		for (ScalarDecl param : this.parameters) {
			param.accept(visitor);
		}
		
		body.accept(visitor);
		
		visitor.visit(this);	
	}
}

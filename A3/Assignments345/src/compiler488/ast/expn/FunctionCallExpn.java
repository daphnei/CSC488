package compiler488.ast.expn;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;

/**
 * Represents a function call with arguments.
 */
public class FunctionCallExpn extends Expn {
    /** The name of the function. */
    private String ident;
    
    private int left;
    private int right;
    /** The arguments passed to the function. */
    private ASTList<Expn> arguments;

    public FunctionCallExpn(String ident, ASTList<Expn> arguments, int left, int right) {
        super();

        this.ident = ident;
        this.arguments = arguments;
        this.left = left;
        this.right = right;
    }

    public ASTList<Expn> getArguments() {
        return arguments;
    }

    public String getIdent() {
        return ident;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print(ident);

        if (arguments.size() > 0) {
            p.print("(");
            arguments.prettyPrintCommas(p);
            p.print(")");
        }
    }

}
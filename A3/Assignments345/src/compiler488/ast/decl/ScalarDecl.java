package compiler488.ast.decl;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDecl extends Declaration {
    public ScalarDecl(String name, Type type,int left, int right) {
        super(name, type,left,right);
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(type + " " + name);
    }

}

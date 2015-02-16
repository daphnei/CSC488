package compiler488.ast.decl;

import compiler488.ast.stmt.Stmt;
import compiler488.ast.type.Type;

/**
 * The common features of declarations.
 */
public abstract class Declaration extends Stmt {
    /** The name of the thing being declared. */
    protected String name;
    protected int left;
    protected int right;

    /** The type of thing being declared. */
    protected Type type = null;

    public Declaration(String name, Type type, int left, int right) {
        super();

        this.name = name;
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public String getName() {
        return name;
    }
    
    public int getLeft(){
        return left;
    }
    
    public int getRight(){
        return right;
    }

    public Type getType() {
        return type;
    }
}
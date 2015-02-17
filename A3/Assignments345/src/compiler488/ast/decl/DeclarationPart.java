package compiler488.ast.decl;

import compiler488.ast.BaseAST;
import compiler488.utilities.IVisitor;

/**
 * The common features of declarations' parts.
 */
public abstract class DeclarationPart extends BaseAST {
    /** The name of the thing being declared. */
    protected String name;
    
    protected int left;
    
    protected int right;

    public DeclarationPart(String name, int left, int right) {
        super();

        this.name = name;
        this.left = left;
        this.right = right;
    }
    
    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public String getName() {
        return name;
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

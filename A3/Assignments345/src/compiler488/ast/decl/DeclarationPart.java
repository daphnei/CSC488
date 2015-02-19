package compiler488.ast.decl;

import compiler488.ast.BaseAST;
import compiler488.utilities.IVisitor;

/**
 * The common features of declarations' parts.
 */
public abstract class DeclarationPart extends BaseAST {
    /** The name of the thing being declared. */
    protected String name;
    
    public DeclarationPart(String name, int left, int right) {
        super();

        this.name = name;
        this.left = left;
        this.right = right;
    }

    public String getName() {
        return name;
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

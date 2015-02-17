package compiler488.ast.decl;

import compiler488.utilities.IVisitor;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
    public ScalarDeclPart(String name, int left, int right) {
        super(name, left, right);
    }

    /**
     * Returns a string describing the name of the object being
     * declared.
     */
    @Override
    public String toString() {
        return name;
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}

}

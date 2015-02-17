package compiler488.ast.type;

import compiler488.utilities.IVisitor;


/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type {
    @Override
    public String toString() {
        return "boolean";
    }

    @Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
}

package compiler488.ast.type;

import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;
import compiler488.utilities.IVisitor;


/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type {	
	
    public BooleanType(int left, int right) {
		super(left, right);
	}

	@Override
    public String toString() {
        return "boolean";
    }

    @Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}
    
	@Override
    public PrimitiveSemType getSemanticType() {
    	return SemType.BOOLEAN;
    }
}

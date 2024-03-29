package compiler488.ast.type;

import compiler488.interfaces.IVisitor;
import compiler488.semantics.types.IntegerSemType;
import compiler488.semantics.types.PrimitiveSemType;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
	
	public IntegerType(int left, int right) {
		super(left, right);
	}
	@Override
	public String toString() {
		return "integer";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}

	@Override
    public PrimitiveSemType getSemanticType() {
    	return IntegerSemType.INTEGER;
    }
}

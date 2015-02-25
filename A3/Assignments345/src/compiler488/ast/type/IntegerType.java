package compiler488.ast.type;

import compiler488.interfaces.IVisitor;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;

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
    	return SemType.INTEGER;
    }
}

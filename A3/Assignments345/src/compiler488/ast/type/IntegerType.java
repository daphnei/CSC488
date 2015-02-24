package compiler488.ast.type;

import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;
import compiler488.utilities.IVisitor;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
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

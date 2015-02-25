package compiler488.ast.type;

import compiler488.ast.BaseAST;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;

/**
 * A placeholder for types.
 */
public abstract class Type extends BaseAST {
	
	public Type(int left, int right) {
		super(left, right);
	}

	//public static final BooleanType BOOLEAN = new BooleanType();
	//public static final IntegerType INTEGER = new IntegerType();
	
	public abstract PrimitiveSemType getSemanticType();
}

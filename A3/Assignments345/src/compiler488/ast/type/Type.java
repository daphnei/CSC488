package compiler488.ast.type;

import compiler488.ast.BaseAST;
import compiler488.symbol.SemType;

/**
 * A placeholder for types.
 */
public abstract class Type extends BaseAST {
	public static final BooleanType BOOLEAN = new BooleanType();
	public static final IntegerType INTEGER = new IntegerType();
	
	public abstract SemType getSemanticType();
}

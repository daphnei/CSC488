package compiler488.ast.type;

import compiler488.ast.BaseAST;

/**
 * A placeholder for types.
 */
public abstract class Type extends BaseAST {
	public static final BooleanType BOOLEAN = new BooleanType();
	public static final IntegerType INTEGER = new IntegerType();
	
	@Override
    public boolean equals(Object obj) {
		return this.getClass().equals(obj.getClass());
	}
}

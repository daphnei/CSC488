package compiler488.semantics.types;

import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;

/**
 * The integer primitive semantic type.
 * 
 * @author adam
 *
 */
public class IntegerSemType extends PrimitiveSemType {
	/**
	 * A re-useable instance of this class.
	 */
    public static final IntegerSemType INTEGER = new IntegerSemType();

	@Override
	public String toString() {
        return "integer";
    }

    @Override
    public Type returnAST() {
        return new IntegerType(0,0);
    }
}

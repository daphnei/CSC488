package compiler488.semantics.types;

import compiler488.ast.type.BooleanType;
import compiler488.ast.type.Type;

/**
 * The boolean primitive semantic type.
 * @author Adam
 *
 */
public class BooleanSemType extends PrimitiveSemType {
	/**
	 * A re-useable instance of this class.
	 */
    public static final BooleanSemType BOOLEAN = new BooleanSemType();

	@Override
    public String toString() {
        return "boolean";
    }

    @Override
    public Type returnAST() {
        return new BooleanType(0,0);
    }
}

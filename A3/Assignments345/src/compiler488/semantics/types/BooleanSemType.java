package compiler488.semantics.types;

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
}

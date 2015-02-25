package compiler488.semantics.types;

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
}

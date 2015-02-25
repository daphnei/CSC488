package compiler488.semantics.types;

public class ErrorSemType extends PrimitiveSemType {
    public static final ErrorSemType ERROR = new ErrorSemType();

	@Override
	public String toString() {
        return "error";
    }
}

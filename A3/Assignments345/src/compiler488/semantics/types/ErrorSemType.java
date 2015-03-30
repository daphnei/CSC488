package compiler488.semantics.types;

import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;

public class ErrorSemType extends PrimitiveSemType {
    public static final ErrorSemType ERROR = new ErrorSemType();

	@Override
	public String toString() {
        return "error";
    }

    @Override
    public Type returnAST() {
        // this will never happen because it would never pass semantic analysis
        return new IntegerType(0,0);
    }
}

package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.type.Type;
import compiler488.semantics.types.ErrorSemType;
import compiler488.semantics.types.PrimitiveSemType;
import compiler488.semantics.types.SemType;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
	private PrimitiveSemType resultType;
	
	public Expn(int left, int right) {
		super(left, right);
		
		this.resultType = null;
	}
	
	public PrimitiveSemType getResultType() {
		return this.resultType;
	}
	
	public void setResultType(PrimitiveSemType type) {
		this.resultType = type;
	}
	
	public boolean hasError() {
		return this.resultType.equals(ErrorSemType.ERROR);
	}
}
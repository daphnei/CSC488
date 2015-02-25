package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.type.Type;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
	
	public Expn(int left, int right) {
		super(left, right);
	}

	private PrimitiveSemType resultType;
	
	public PrimitiveSemType getResultType() {
		return this.resultType;
	}
	
	public void setResultType(PrimitiveSemType type) {
		this.resultType = type;
	}
}
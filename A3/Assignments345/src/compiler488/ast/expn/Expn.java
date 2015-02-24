package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.type.Type;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
	private Type resultType;
	
	public Type getResultType() {
		return this.resultType;
	}
	
	public void setResultType(Type type) {
		this.resultType = type;
	}
}
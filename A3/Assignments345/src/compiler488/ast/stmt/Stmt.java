package compiler488.ast.stmt;

import compiler488.ast.BaseAST;
import compiler488.codegen.Quadruple;
import compiler488.semantics.types.PrimitiveSemType;

/**
 * A placeholder for statements.
 */
public abstract class Stmt extends BaseAST {
	
	private Quadruple codeLine;

	public Stmt(int left, int right) {
		super(left, right);
	}
	
	private void setCodeAddress(Quadruple codeLine) {
		this.codeLine = codeLine;
	}

}

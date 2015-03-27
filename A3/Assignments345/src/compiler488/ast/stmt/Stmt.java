package compiler488.ast.stmt;

import compiler488.ast.BaseAST;
import compiler488.semantics.types.PrimitiveSemType;

/**
 * A placeholder for statements.
 */
public abstract class Stmt extends BaseAST {
	public Stmt(int left, int right) {
		super(left, right);
	}
}

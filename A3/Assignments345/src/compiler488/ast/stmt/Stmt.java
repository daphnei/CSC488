package compiler488.ast.stmt;

import compiler488.ast.BaseAST;

/**
 * A placeholder for statements.
 */
public abstract class Stmt extends BaseAST {
	/**
	 * Return the right and left columns of this statement 
	 * in the input script file.
	 */
	protected int left;
	protected int right;
	
	/**
	 * @return the column number of the beginning of this statement.
	 */
    public int getLeft() {
        return left;
    }

    /**
     * @return the column number of the right of this statement.
     */
    public int getRight() {
        return right;
    }
}

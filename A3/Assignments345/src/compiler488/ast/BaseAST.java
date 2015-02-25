package compiler488.ast;

import compiler488.utilities.IVisitableElement;

/**
 * Base class implementation for the AST hierarchy.
 *
 * This is a convenient place to add common behaviours.
 *
 * @author Dave Wortman, Marsha Chechik, Danny House, Peter McCormick
 */
public abstract class BaseAST implements AST, IVisitableElement {
	

	/**
	 * Return the right and left columns of this statement
	 * in the input script file.
	 */
	protected int left;
	protected int right;
	
	/**
	 * Default constructor.
	 *
	 * <p>Add additional information to your AST tree nodes here.</p>
	 */
	public BaseAST(int left, int right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * A default pretty-printer implementation that uses <code>toString</code>.
	 *
	 * @param p the printer to use
	 */
	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(toString());
	}

	/**
	 * @return the column number of the beginning of this statement.
	 */
	public int getLineNumber() {
		return left;
	}

	/**
	 * @return the column number of the right of this statement.
	 */
	public int getColumnNumber() {
		return right;
	}

}

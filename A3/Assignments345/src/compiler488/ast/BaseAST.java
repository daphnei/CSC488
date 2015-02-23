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
	public static enum ResultType {
		INTEGER("integer"),
		BOOLEAN("boolean"),
		EXPRESSION("expression"),
		ANON_FUNCTION("anonymous function"),
		PARAMETER_NAME("parameter name"),
		VARIABLE_NAME("variable name"),
		ARRAY_ELEMENT("array element"),
		FUNCTION("function");
		
		private String humanReadable;
		private ResultType(String humanReadable) {
			this.humanReadable = humanReadable;
		}
		
		@Override
		public String toString() {
			return this.humanReadable;
		}
	};
	
	private ResultType resultType;

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
	public BaseAST() {
		this.resultType = null;
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
	public int getLeftColumnNumber() {
		return left;
	}

	/**
	 * @return the column number of the right of this statement.
	 */
	public int getRightColumnNumber() {
		return right;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
}

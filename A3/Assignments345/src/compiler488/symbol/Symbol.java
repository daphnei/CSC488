package compiler488.symbol;

import compiler488.ast.type.Type;

public class Symbol {
	/**
	 * My identifier
	 */
	private String identifier;

	/**
	 * My scope index.
	 */
	private int scopeIndex;

	private Type type;

	public Symbol(String identifier, int scopeIndex, Type type) {
		this.identifier = identifier;
		this.scopeIndex = scopeIndex;
		this.type = type;
	}

	/**
	 * @return The index of the scope in which this symbol was declared.
	 */
	public int getScope() {
		return this.scopeIndex;
	}
}

package compiler488.symbol;

import compiler488.semantics.types.SemType;

public class Symbol {
	/**
	 * My identifier
	 */
	private String identifier;

	/**
	 * My scope index.
	 */
	private int scopeIndex;

	/**
	 * The type associated with this symbol.
	 */
	private SemType type;

	public Symbol(String identifier, int scopeIndex, SemType type) {
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
	
	public SemType getType() {
		return this.type;
	}
}

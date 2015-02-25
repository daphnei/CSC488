package compiler488.symbol;

import compiler488.ast.type.Type;
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
	 * TODO: This is wrong. We should be storing something that allows for procedure.
	 * function types as well?
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

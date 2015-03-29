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

	/**
	 * The address offset from this symbol's lexical level.
	 */
	int llOffset;
	
	public Symbol(String identifier, int scopeIndex, SemType type) {
		this.identifier = identifier;
		this.scopeIndex = scopeIndex;
		this.type = type;
	}
	
	public void setOffset(int offset) {
		this.llOffset = offset;
	}
	
	public int getOffset() {
		return this.llOffset;
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

	public short getLexicalLevel(SymbolTable table) {
		return table.scopes.get(table.scopes.size() - 1 - this.scopeIndex).getLexicalLevel();
	}
}

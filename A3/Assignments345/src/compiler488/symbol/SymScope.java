package compiler488.symbol;

import compiler488.runtime.Machine;

public class SymScope {
	private ScopeType scopeType;
	
	/**
	 * The lexical level corresponding to this scope.
	 * This is only used in code generation.
	 */
	private short lexicalLevel;
	
	/**
	 * The offset to the first address in this lexical level
	 * that has not been allocated yet.
	 */
	private short freeOffset;
	
	public SymScope(ScopeType scopeType) {
		this(scopeType, Machine.UNDEFINED);
	}
	
	public SymScope(ScopeType scopeType, short lexicalLevel) {
		this.scopeType = scopeType;
		this.lexicalLevel = lexicalLevel;
		this.freeOffset = 0;
	}
	
	public ScopeType getScopeType() {
		return this.scopeType;
	}
	
	public boolean isMajor() {
		return this.scopeType.isMinor();
	}
	
	public boolean isMinor() {
		return this.scopeType.isMinor();
	}
	
	/**
	 * Assigns space in the lexical level for a new variable. 
	 * Increments the free offset to mark the space now in use. 
	 * @param varSize The number of shorts the variable should take up in memory.
	 *                For an integer or a boolean, this will be 1. For an array, 
	 *                this will be larger.
	 * @return The offset to the first memory address of the variable.
	 */
	public int assignSpaceForNewVariable(int varSize) {
		int addressOfVar = this.freeOffset;
		this.freeOffset += varSize;
		return addressOfVar;
	}
	
	public short getLexicalLevel() {
		return this.lexicalLevel;
	}
}

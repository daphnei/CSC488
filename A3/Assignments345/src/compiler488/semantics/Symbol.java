package compiler488.semantics;

import compiler488.ast.type.Type;

public class Symbol {
	/**
	 * My identifier
	 */
	private String identifier;
	
	/** 
	 * My scope index. Maybe this should be some sort of Scope object instead?
	 */
	private int scopeIndex;
	
	private Type type;
	
	public Symbol(String identifier, int scopeIndex, Type type) {
		this.identifier = identifier;
		this.scopeIndex = scopeIndex;
		this.type = type;
	}
}

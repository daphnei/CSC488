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
	private  SymTableScope scope;
	
	private Type type;
	
	public Symbol(String identifier, SymTableScope scope, Type type) {
		this.identifier = identifier;
		this.scope = scope;
		this.type = type;
	}
	
	public SymTableScope getScope() {
		return this.scope;
	}
}

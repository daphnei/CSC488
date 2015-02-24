package compiler488.semantics;

import compiler488.ast.AST;

public class SymbolConflictException extends SemanticErrorException {
	
	public String symbolName;
	
	public SymbolConflictException(String symbolName) {
		this.symbolName = symbolName;
	}
}

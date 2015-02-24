package compiler488.semantics;

import compiler488.ast.AST;

public class UndeclaredSymbolException extends SemanticErrorException {
	public String symbolName;
	
	public UndeclaredSymbolException(String symbolName) {
		this.symbolName = symbolName;
	}
}

package compiler488.exceptions;

import compiler488.ast.AST;

public class UndeclaredSymbolException extends SemanticErrorException {	
	public UndeclaredSymbolException(String symbolName) {
		super("The identifier '" + symbolName + "' does not exist in this scope.");
	}
}
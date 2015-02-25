package compiler488.exceptions;

import compiler488.ast.AST;

public class SymbolConflictException extends SemanticErrorException {
		
	public SymbolConflictException(String symbolName) {
		super("The identifier '" + symbolName + "' has already been declared in this scope.");
	}
}

package compiler488.exceptions;

import compiler488.ast.AST;

public class InvalidScopeException extends SemanticErrorException {
	public InvalidScopeException() {
		super("Trying to operate on a non-existent scope.");
	}
}

package compiler488.exceptions.semantic;

import compiler488.ast.AST;

/**
 * An exception that is thrown when no scope exists, but the user
 * tries to do an operation that requies an open scope.
 * @author daphne
 *
 */
public class InvalidScopeException extends SemanticErrorException {
	public InvalidScopeException() {
		super("Trying to operate on a non-existent scope.");
	}
}

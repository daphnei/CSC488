package compiler488.exceptions;

import compiler488.ast.AST;

/**
 * An exception that is thrown when the user attempts
 * to retrieve a symbol that has not been declared
 * in the current scope.
 * 
 * @author daphne
 *
 */
public class UndeclaredSymbolException extends SemanticErrorException {	
	public UndeclaredSymbolException(String symbolName) {
		super("The identifier '" + symbolName + "' does not exist in this scope.");
	}
}
package compiler488.semantics;

import compiler488.ast.AST;

/**
 * This error should be thrown when a method wants to report a found semantic error.
 * The message encoded should be user friendly.
 * 
 * @author daphne
 */
public class SemanticErrorException extends Exception {
	
	public SemanticErrorException() {
	}
	
	public SemanticErrorException(String message) {
		super(message);
	}
}

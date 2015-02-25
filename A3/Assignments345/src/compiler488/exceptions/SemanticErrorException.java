package compiler488.exceptions;

import compiler488.ast.AST;

/**
 * This exception should be thrown when a semantic error occurs.
 * The message encoded should be user friendly.
 * 
 * @author daphne
 */
public class SemanticErrorException extends Exception {
		
	public SemanticErrorException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		
		if (message == null || message.isEmpty()) {
			return "Unknown Error.";
		} else {
			return super.getMessage();
		}
	}
}

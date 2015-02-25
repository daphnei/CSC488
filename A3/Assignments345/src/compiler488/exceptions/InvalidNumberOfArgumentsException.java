package compiler488.exceptions;

public class InvalidNumberOfArgumentsException extends SemanticErrorException {
	public InvalidNumberOfArgumentsException(String identifier, int expected, int actual) {
		super("Expected '" + identifier + "' to have " + expected + " arguments but it instead had " + actual + ".");
	}
}

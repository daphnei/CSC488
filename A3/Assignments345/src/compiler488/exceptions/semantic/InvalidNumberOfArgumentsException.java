package compiler488.exceptions.semantic;

/**
 * An exception that is thrown when a routine is called with the inccorect
 * number of arguments.
 */
public class InvalidNumberOfArgumentsException extends SemanticErrorException {
	public InvalidNumberOfArgumentsException(String identifier, int expected, int actual) {
		super("Expected '" + identifier + "' to have " + expected + " arguments but it instead had " + actual + ".");
	}
}

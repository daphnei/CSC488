package compiler488.exceptions.semantic;

/**
 * An Exception that occurs when the user attempts to declare
 * an array with invalid bounds.
 * 
 * @author daphne
 *
 */
public class InvalidArrayBoundsException extends SemanticErrorException {
	public InvalidArrayBoundsException(String name) {
		super("The array '" + name + "' has been declared with invalid bounds.");
	}
}

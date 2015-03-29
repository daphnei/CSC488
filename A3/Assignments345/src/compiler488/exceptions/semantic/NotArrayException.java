package compiler488.exceptions.semantic;

/**
 * An exception that is thrown when the user tries to use a variable
 * as an array when that variable is not an array.
 * @author daphne
 *
 */
public class NotArrayException extends SemanticErrorException {
	public NotArrayException(String name) {
		super("Trying to index a non-array variable " + name + ".");
	}
}

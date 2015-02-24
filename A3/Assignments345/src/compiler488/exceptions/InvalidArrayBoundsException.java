package compiler488.exceptions;

public class InvalidArrayBoundsException extends SemanticErrorException {
	public InvalidArrayBoundsException(String name) {
		super("The array '" + name + "' has been declared with invalid bounds.");
	}
}

package compiler488.exceptions;

public class NotArrayException extends SemanticErrorException {
	public NotArrayException(String name) {
		super("Trying to index a non-array variable " + name + ".");
	}
}

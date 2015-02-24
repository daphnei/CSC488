package compiler488.semantics;

public class InvalidArrayBoundsException extends SemanticErrorException {
	public String name;

	public InvalidArrayBoundsException(String name) {
		this.name = name;
	}
}

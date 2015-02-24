package compiler488.semantics;

public class NotArrayException extends SemanticErrorException {
	public String name;
	public NotArrayException(String name) {
		this.name = name;
	}
}

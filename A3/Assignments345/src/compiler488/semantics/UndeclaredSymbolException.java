package compiler488.semantics;

public class UndeclaredSymbolException extends SemanticErrorException {
	public String symbolName;
	
	public UndeclaredSymbolException(String symbolName) {
		this.symbolName = symbolName;
	}
}

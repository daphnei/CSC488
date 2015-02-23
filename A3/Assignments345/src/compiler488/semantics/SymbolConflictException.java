package compiler488.semantics;

public class SymbolConflictException extends SemanticErrorException {
	
	public String symbolName;
	
	public SymbolConflictException(String symbolName) {
		this.symbolName = symbolName;
	}
}

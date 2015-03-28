package compiler488.exceptions;


/**
 * An exception that is thrown when the user attempts to
 * add a symbol to the symbol table whose name conflicts
 * with a symbol that already exists in the symbol table.
 * 
 * @author daphne
 *
 */
public class SymbolConflictException extends SemanticErrorException {
		
	public SymbolConflictException(String symbolName) {
		super("The identifier '" + symbolName + "' has already been declared in this scope.");
	}
}

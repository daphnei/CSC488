package compiler488.symbol;

import java.util.HashMap;
import java.util.Stack;

import compiler488.ast.type.Type;
import compiler488.exceptions.InvalidScopeException;
import compiler488.exceptions.SemanticErrorException;
import compiler488.exceptions.SymbolConflictException;

public class SymbolTable {
	protected static enum ScopeType {
		MAJOR, MINOR
	};

	/**
	 * The table maps from the identifier name (such as foo) to a list of
	 * symbols using that identifier name. In each list, the first item should
	 * be the one in the nearest-most scope, and the last item, the one in the
	 * farthest.
	 */
	private HashMap<String, Stack<Symbol>> table;

	/**
	 * The index of the innermost opened scope.
	 */
	private int curScopeIndex;

	/**
	 * Keeps track of whether each scope is major or minor.
	 */
	private Stack<ScopeType> scopeTypes;
	
	public SymbolTable() {
		// Don't use this. Instead use Initialize. This allows us to recycle one
		// SymbolTable object.
	}

	/**
	 * Initialize - called once by semantic analysis at the start of compilation
	 */
	public void Initialize() {
		this.table = new HashMap<String, Stack<Symbol>>();
		this.scopeTypes = new Stack<SymbolTable.ScopeType>();

		this.curScopeIndex = -1; // Set this to -1 so that the first scope
									// created will have index 0.
	}

	/**
	 * Finalize - called once by Semantics at the end of compilation May be
	 * unnecessary
	 */
	public void Finalize() {
		this.table.clear();
		this.scopeTypes.clear();
	}

	public void openMajorScope() {
		this.curScopeIndex++;
		this.scopeTypes.push(ScopeType.MAJOR);
	}

	public void openMinorScope() {
		this.curScopeIndex++;
		this.scopeTypes.push(ScopeType.MINOR);
	}

	/**
	 * Closes the current scope and deletes all of the symbols that were defined
	 * in it.
	 * 
	 * @throws SemanticError
	 *             When there is no existing open scope.
	 */
	public void closeCurrentScope() throws SemanticErrorException {
		this.checkIfThereIsAnyScope();

		// If the current scope is major, destroy all the symbols that were declared in it.
		if (this.scopeTypes.peek() == ScopeType.MAJOR) {			
			for (String identifier : this.table.keySet()) {
				Stack<Symbol> symbols = this.table.get(identifier);
	
				// Check if any symbols exist for this identifier.
				if (symbols != null && !symbols.isEmpty()) {
					// If symbols do exist, check if the first one is in the current
					// scope.
					if (symbols.peek().getScope() >= this.curScopeIndex) {
						symbols.pop();
					}
				}
			}
		}

		// All of the symbols that were declared in the current scope have been
		// removed.
		// Now decrement the scope index.
		this.curScopeIndex--;
		this.scopeTypes.pop();
	}

	/**
	 * Returns the symbol with the specified identifier that is located in the
	 * nearest most scope. Returns null if no symbol exists with the specified
	 * identifier.
	 * 
	 * @param identifier
	 *            The identifier to look for.
	 * @return
	 * @throws SemanticError
	 *            TODO: Add helpful information to the error being thrown.
	 */
	public Symbol retrieveSymbol(String identifier) throws SemanticErrorException {
		this.checkIfThereIsAnyScope();

		Stack<Symbol> symbols = this.table.get(identifier);

		if (symbols == null || symbols.isEmpty()) {
			return null;
		} else {
			return symbols.peek();
		}
	}

	/**
	 * Adds a new symbol to the currently open scope
	 * 
	 * @param identifier
	 *            The variable name.
	 * @param type
	 *            The type of the variable (boolean of integer).
	 * @return the newly created symbol, or null if it was not possible to
	 *         create a symbol.
	 * @throws SemanticError
	 */
	public Symbol addSymbolToCurScope(String identifier, SemType type) throws SemanticErrorException {
		if (type == null) {
			throw new SemanticErrorException("The type passed to the declaration is null!");
		}
		
		this.checkIfThereIsAnyScope();

		// Get the list of symbols associated with this identifier.
		Stack<Symbol> symbols = this.table.get(identifier);

		if (symbols == null || symbols.isEmpty()) {
			// If "symbols" is empty or null, then no symbol with this name
			// exists. Yay!

			if (symbols == null) {
				symbols = new Stack<Symbol>();
				this.table.put(identifier, symbols);
			}

			// Create the new symbol to the front of the symbol list.
			Symbol newSymbol = new Symbol(identifier, this.curScopeIndex, type);
			symbols.add(newSymbol);

			return newSymbol;
		} else if (symbols.get(0).getScope() == this.curScopeIndex) {
			// If a symbol with the input identifier already exists in the
			// current scope, then it is an error.
			throw new SymbolConflictException(identifier);
		} else {
			// A symbol with the input identifier exists but it is in an upper
			// scope.

			// This is unacceptable if the current scope is minor.
			if (this.scopeTypes.peek() == ScopeType.MINOR) {
				throw new SymbolConflictException(identifier);
			}

			// The current scope is major, so it is ok to redeclare a variable
			// that was declared
			// in an upper scope.
			Symbol newSymbol = new Symbol(identifier, this.curScopeIndex, type);
			symbols.add(newSymbol);

			return newSymbol;
		}
	}

	/**
	 * @return the index of the currently open scope, or -1 if no scope has been
	 *         opened yet.
	 */
	public int getCurrentScope() {
		return this.curScopeIndex;
	}

	/**
	 * Checks is any scope exists.
	 * 
	 * @throws SemanticError
	 *             if no scope exists.
	 */
	private void checkIfThereIsAnyScope() throws InvalidScopeException {
		if (this.curScopeIndex < 0) {
			// There is no open scope. This is a semantic error.
			throw new InvalidScopeException();
		}
	}
}

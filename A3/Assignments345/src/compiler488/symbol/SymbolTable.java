package compiler488.symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import compiler488.exceptions.semantic.InvalidScopeException;
import compiler488.exceptions.semantic.SemanticErrorException;
import compiler488.exceptions.semantic.SymbolConflictException;
import compiler488.semantics.types.SemType;

public class SymbolTable {
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
	 * Keeps track of the type of each open scope.
	 * The most recently opened scope will be at the end.
	 */
	protected LinkedList<SymScope> scopes;
	
	
	public SymbolTable() {
		// Don't use this. Instead use Initialize. This allows us to recycle one
		// SymbolTable object.
	}

	/**
	 * Initialize - called once by semantic analysis at the start of compilation
	 */
	public void Initialize() {
		this.table = new HashMap<String, Stack<Symbol>>();
		this.scopes = new LinkedList<SymScope>();

		this.curScopeIndex = -1; // Set this to -1 so that the first scope
	                             // created will have index 0.
	}

	/**
	 * Finalize - called once by Semantics at the end of compilation May be
	 * unnecessary
	 */
	public void Finalize() {
		this.table.clear();
		this.scopes.clear();
	}

	/**
	 * Opens a scope of the given type.
	 * @param type
	 * 			The type of the scope determines if it is major or minor.
	 */
	public void openScope(ScopeType type) {
		this.curScopeIndex++;
		this.scopes.push(this.createNewScope(type));	
	}

	/**
	 * Created a new scope and returns it.
	 * @param type The type of the new scope.
	 */
	protected SymScope createNewScope(ScopeType type) {
		return new SymScope(type);
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
		if (this.scopes.peek().isMajor()) {			
			for (String identifier : this.table.keySet()) {
				Stack<Symbol> symbols = this.table.get(identifier);
	
				// Check if any symbols exist for this identifier.
				if (symbols != null && !symbols.isEmpty()) {
					// If symbols do exist, check if the first one is in the current scope.
					while (!symbols.isEmpty() && symbols.peek().getScope() >= this.curScopeIndex) {
						symbols.pop();
					}
				}
			}
		}

		// All of the symbols that were declared in the current scope have been removed.
		// Now decrement the scope index.
		this.curScopeIndex--;
		this.scopes.pop();
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
			symbols.push(newSymbol);

			return newSymbol;
		} else if (symbols.peek().getScope() == this.curScopeIndex) {
			// If a symbol with the input identifier already exists in the
			// current scope, then it is an error.
			throw new SymbolConflictException(identifier);
		} else {
			// A symbol with the input identifier exists but it is in an upper
			// scope.

			// This is unacceptable if there is no major scope between the current and the last.
			int i = this.searchForLastMajorScope();
			if (symbols.peek().getScope() >= i) {
				throw new SymbolConflictException(identifier);
			}

			// The current scope is major, so it is ok to redeclare a variable
			// that was declared
			// in an upper scope.
			Symbol newSymbol = new Symbol(identifier, this.curScopeIndex, type);
			symbols.push(newSymbol);

			return newSymbol;
		}
	}
	
	/**
	 * Looks down the scope stack for the last scope that was major.
	 * @return the found scope, or -1 if none.
	 */
	public int searchForLastMajorScope() {
		int currentScope = this.curScopeIndex;
		for (SymScope scope : this.scopes) {
			if (scope.isMajor()) {
				return currentScope;
			}
			currentScope--;
		}
		
		return -1;
	}
	
	/**
	 * Checks the linearly through all open scopes, looking for a scope of the specified type.
	 * If it finds the type, it returns true. If it encounters a forbidden type while searching
	 * it returns false.
	 * 
	 * @param searchTarget 
	 * @return True if a scope of the input type is open, false otherwise
	 */
	public boolean searchScopesForType(ScopeType searchTarget, Set<ScopeType> forbiddenSet) {
		// The lowest index scope is the last element in the scopeTypes list.
		// this.curScopeIndex corresponds to the first element in the scopeTypes list.	
		for (SymScope scope : this.scopes) {
			if (forbiddenSet.contains(scope.getScopeType())) {
				return false;
			}
			if (scope.getScopeType().equals(searchTarget)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @return the index of the currently open scope, or -1 if no scope has been
	 *         opened yet.
	 */
	public int getCurrentScopeIndex() {
		return this.curScopeIndex;
	}

	/**
	 * Returns the scope object associated with the currently open scope.
	 * @return
	 */
	public SymScope getCurrentScope() {
		return this.scopes.peek();
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

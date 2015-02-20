package compiler488.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import compiler488.ast.type.Type;
import compiler488.semantics.SemanticError;
import compiler488.symbol.SymTableScope.ScopeType;

public class SymbolTable {
	/**
	 * The table maps from the identifier name (such as foo)
	 * to a list of symbols using that identifier name.
	 * In each list, the first item should be the one in the nearest-most scope,
	 * and the last item, the one in the farthest.
	 */
	private HashMap<String, ArrayList<Symbol>> table;

	/**
	 * The stack of open scopes.
	 */
	private Stack<SymTableScope> scopes;

	public SymbolTable()
	{
		this.table = new HashMap<String, ArrayList<Symbol>>();
		this.scopes = new Stack<SymTableScope>();
	}

	/**  Initialize - called once by semantic analysis
	 *                at the start of  compilation
	 *                May be unnecessary if constructor
	 *                does all required initialization
	 */
	public void Initialize() {

		/**   Initialize the symbol table
		 *	Any additional symbol table initialization
		 *  GOES HERE
		 */

	}

	/**  Finalize - called once by Semantics at the end of compilation
	 *              May be unnecessary
	 */
	public void Finalize() {

		/**  Additional finalization code for the
		 *  symbol table  class GOES HERE.
		 *
		 */
	}

	public void openMajorScope() {
		SymTableScope newScope = new SymTableScope(this.scopes.size(), ScopeType.MAJOR);
		this.scopes.add(newScope);
	}

	public void openMinorScope() {
		SymTableScope newScope = new SymTableScope(this.scopes.size(), ScopeType.MINOR);
		this.scopes.add(newScope);
	}

	public void closeCurrentScope() {
		//TODO: Implement this
	}


	/**
	 * Returns the symbol with the specified identifier that is 
	 * located in the nearest most scope. Returns null if no
	 * symbol exists with the specified identifier.
	 * @param identifier The identifier to look for.
	 * @return
	 */
	public Symbol retrieveSymbol(String identifier) {
		ArrayList<Symbol> symbols = this.table.get(identifier);

		if (symbols == null || symbols.isEmpty()) {
			return null;
		} else {
			return symbols.get(0);
		}
	}
	
	public void addSymbolToCurScope(String identifier, Type type) throws SemanticError {
		//TODO: Implement this

		//Get the list of symbols associated with this identifier.
		ArrayList<Symbol> symbols = this.table.get(identifier);

		if (symbols == null || symbols.isEmpty()) {
			//If "symbols" is empty or null, then no symbol with this name exists. Yay!

			//Create the new symbol to the front of the symbol list.
			Symbol newSymbol = new Symbol(identifier, this.curScope(), type);
			symbols.add(0, newSymbol);
		}
		else if (symbols.get(0).getScope() == this.curScope()) {
			//If a symbol with the input identifier already exists in this scope, then it is an error.

			throw new SemanticError();
		}
		else {
			//A symbol with the input identifier exists but it is in an upper scope.

			//TODO: Figure out what should happen here. The behavior should be different
			//      if the symbol is in a  major vs minor scope?
		}
	}

	private SymTableScope curScope() {
		return this.scopes.peek();
	}
}

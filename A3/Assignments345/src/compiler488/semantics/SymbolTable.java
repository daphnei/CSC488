package compiler488.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import compiler488.ast.type.Type;
import compiler488.semantics.SymTableScope.ScopeType;

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

	public void newMajorScope() {
		SymTableScope newScope = new SymTableScope(this.scopes.size(), ScopeType.MAJOR);
		this.scopes.add(newScope);
	}
	
	public void newMinorScope() {
		SymTableScope newScope = new SymTableScope(this.scopes.size(), ScopeType.MINOR);
		this.scopes.add(newScope);
	}
	
	public void closeCurrentScope() {
		//TODO: Implement this
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

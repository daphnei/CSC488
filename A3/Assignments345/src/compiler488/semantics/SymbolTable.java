package compiler488.semantics;

import java.util.ArrayList;
import java.util.HashMap;

import compiler488.ast.type.Type;

public class SymbolTable {
	/**
	 * The table maps from the identifier name (such as foo) 
	 * to a list of symbols using that identifier name.
	 */
	private HashMap<String, ArrayList<Symbol>> table;
	
	public SymbolTable()
	{
		
	}

	public void newMajorScope() {
		//TODO: Implement this
	}
	
	public void newMinorScope() {
		//TODO: Implement this
	}
	
	public void closeCurrentScope() {
		//TODO: Implement this
	}
	
	public void addSymbolToCurScope(String identifier, Type type) {
		//TODO: Implement this
	}
}

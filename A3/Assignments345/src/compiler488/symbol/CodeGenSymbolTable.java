package compiler488.symbol;

public class CodeGenSymbolTable extends SymbolTable {
	/**
	 * The highestmost lexical level current in use.
	 */
	short openLexicalLevel;
	
	public CodeGenSymbolTable() {
		this.openLexicalLevel = -1;
	}
	
	@Override
	protected SymScope createNewScope(ScopeType type) {
		this.openLexicalLevel++;
		SymScope scope = new SymScope(type, this.openLexicalLevel);
		return scope;
	}
}

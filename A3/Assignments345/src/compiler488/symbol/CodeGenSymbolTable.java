package compiler488.symbol;

public class CodeGenSymbolTable extends SymbolTable {
	
	public CodeGenSymbolTable() {
		
	}
	
	private int getOpenLexicalLevel() {
		if (this.scopes.isEmpty()) {
			return -1;
		}
		return this.scopes.peek().getLexicalLevel();
	}
	
	@Override
	protected SymScope createNewScope(ScopeType type) {
		short nextLevel = (short)(this.getOpenLexicalLevel() + (type.isMajor() ? 1 : 0));
		SymScope scope = new SymScope(type, nextLevel);
		return scope;
	}
}

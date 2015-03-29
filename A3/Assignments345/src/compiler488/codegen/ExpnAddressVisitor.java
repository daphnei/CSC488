package compiler488.codegen;

import compiler488.ast.expn.IdentExpn;
import compiler488.runtime.Machine;
import compiler488.semantics.NodeVisitor;
import compiler488.symbol.SymScope;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

/**
 * Visits an expression and returns the address of the variable
 * resulting from the expression.
 * 
 * This is used for getting the address of the lefthand side
 * of variable assignments.
 * 
 * @author daphne
 *
 */
public class ExpnAddressVisitor extends NodeVisitor {
	private SymbolTable symbolTable;
	private CodeWriter writer;
	
	public ExpnAddressVisitor(SymbolTable symbolTable, CodeWriter writer) {
		this.symbolTable = symbolTable;
		this.writer = writer;
	}
	
	@Override
	public void visit(IdentExpn visitable) {
		String varName = visitable.getIdentifier();
		
		Symbol symbol = this.symbolTable.retrieveSymbol(varName);
		SymScope scope = this.symbolTable.getCurrentScope();
		
		// Push the address of this variable on to the top of the stack.
		this.writer.writeRawAssemply(Machine.ADDR, symbol.getLexicalLevel(), symbol.getOffset());
	}
}

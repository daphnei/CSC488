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
	private CodeGenVisitor codeGenVisitor;
	
	public ExpnAddressVisitor(SymbolTable symbolTable, CodeWriter writer, CodeGenVisitor codeGenVisitor) {
		this.symbolTable = symbolTable;
		this.writer = writer;
		this.codeGenVisitor = codeGenVisitor;
	}
	
	@Override
	public void visit(IdentExpn visitable) {
		String varName = visitable.getIdentifier();
				
		// Push the address of this variable on to the top of the stack.
		this.writer.writeSymbolAddress(varName);
	}
}

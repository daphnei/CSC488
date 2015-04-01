package compiler488.codegen;

import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.runtime.Machine;
import compiler488.semantics.NodeVisitor;
import compiler488.semantics.types.ArraySemType;
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
		this.writer.writeSymbolAddress(varName, symbolTable);
	}
	
	@Override
	public void visit(SubsExpn visitable) {		
		// Retrieve the symbol semtype associated with this expression.
		ArraySemType semType = (ArraySemType) this.symbolTable.retrieveSymbol(visitable.getVariable()).getType();
		
		// Evaluate the left and right subscripts, pushing the results on to the stack.
		visitable.getSubscript1().accept(this.codeGenVisitor);
		
		// Add the dimension offset to this subscript.
		this.writer.writeSubscriptOffsetAndBoundsCheck(semType.getOffsetDim1(), semType.getLengthDim1(), visitable.getLineNumber());
		
		if (visitable.getSubscript2() == null) {
			// Push the first memory address of the array on to the stack.
			this.writer.writeSymbolAddress(visitable.getVariable(), symbolTable);
			
			// If there is only one dimension, our lives our easy, and a simple add is enough 
			// to get to the proper memory address.
			this.writer.writeRawAssembly(Machine.ADD);
		} else {
			// Go "forward" by the number of rows specified by the first subscript.
			// This can be done by multiple the first subscript by the number of elements per row.
			this.writer.writeRawAssembly(Machine.PUSH, semType.getLengthDim2());
			this.writer.writeRawAssembly(Machine.MUL);
			
			// Add the second subscript to the result in order to get the offset within the current row. 
			visitable.getSubscript2().accept(this.codeGenVisitor);
			
			// Add the dimension offset to this subscript.
			this.writer.writeSubscriptOffsetAndBoundsCheck(semType.getOffsetDim2(), semType.getLengthDim2(), visitable.getLineNumber());
                        
                        // Push the first memory address of the array on to the stack.
			this.writer.writeSymbolAddress(visitable.getVariable(), symbolTable);
			
			// Do one more add to complete the computation.
			this.writer.writeRawAssembly(Machine.ADD);
		}
		//ArrayIndexOutOfBoundsException
		// Once this point has been reached, the code to find the address of the desired 
		// position in the array has been generated. This value has been pushed to the top
		// of the stack.
	}
}

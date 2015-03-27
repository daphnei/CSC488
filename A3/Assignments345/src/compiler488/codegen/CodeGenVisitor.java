package compiler488.codegen;

import java.util.LinkedList;

import compiler488.ast.Printable;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.Stmt;
import compiler488.runtime.ExecutionException;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.semantics.NodeVisitor;

public class CodeGenVisitor extends NodeVisitor {

	public static final boolean DEBUGGING = false;

	private CodeWriter writer;

	public CodeGenVisitor() {
		
	}

	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		this.writer = new CodeWriter();
		program.accept(this);
		this.writer.printWrittenCode();		

		Machine.setPC((short)0); /* where code to be executed begins */
		Machine.setMSP(this.writer.getCurrentProgramLength()); /* where memory stack begins */
		Machine.setMLP((short)(Machine.memorySize - 1)); /* */
	}

	@Override
	public void visit(Program visitable) {
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TRON);
		}
		super.visit(visitable);
		if (DEBUGGING) {
			writer.writeRawAssembly(Machine.TROFF);
		}
		writer.writeRawAssembly(Machine.HALT);
	}
	
	@Override
	public void visit(IfStmt visitable) {
		// DO NOT CALL SUPER
		
		// TODO: Handle conditional AND and OR
		// Leaves the result on top of the stack.
		visitable.getCondition().accept(this);
		
		
		for (Stmt trueStmt : visitable.getWhenTrue()) {
			trueStmt.accept(this);
		}

		// If there is no else, then "whenFalse" will be null.
		if (visitable.getWhenFalse() != null) {
			for (Stmt falseStmt : visitable.getWhenFalse()) {
				falseStmt.accept(this);
			}
		}
	}
	
	@Override
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);		
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
	}
	
	@Override
	public void visit(IntConstExpn visitable) {
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue());
	}

	@Override
	public void visit(PutStmt visitable) {
		// DO NOT CALL SUPER
		
		for (Printable printable : visitable.getOutputs()) {
			if (printable instanceof TextConstExpn) {
				// Handle special case of printing text constants.
				TextConstExpn expn = (TextConstExpn)printable;
				for(char c : expn.getValue().toCharArray()) {
					this.writer.writeRawAssembly(Machine.PUSH, c);
					this.writer.writeRawAssembly(Machine.PRINTC);
				}
			} else if (printable instanceof SkipConstExpn) {
				// Handle special case of printing newlines.
				this.writer.writeRawAssembly(Machine.PUSH, '\n');
				this.writer.writeRawAssembly(Machine.PRINTC);
			} else if (printable instanceof Expn) {
				// Handle regular expressions.
				Expn expn = (Expn)printable;
				expn.accept(this);
				this.writer.writeRawAssembly(Machine.PRINTI);
			} else {
				System.out.println("WARNING: Cannot handle printing non-experssion!");
			}
		}
	}
}

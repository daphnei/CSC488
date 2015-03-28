package compiler488.codegen;

import java.util.LinkedList;

import compiler488.ast.Printable;
import compiler488.ast.expn.AnonFuncExpn;
import compiler488.ast.expn.ArithExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.Stmt;
import compiler488.compiler.Main;
import compiler488.runtime.ExecutionException;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.semantics.NodeVisitor;

public class CodeGenVisitor extends NodeVisitor {

	public static final boolean DEBUGGING = true;

	private CodeWriter writer;

	public CodeGenVisitor() {
		
	}

	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		this.writer = new CodeWriter();
		program.accept(this);
		this.writer.printWrittenCode();	
		if (!this.writer.isCompletelyPatched()) {
			Main.errorOccurred = true;
			System.out.print("ERROR: We have unpatched branch statements! Please fix them!");
		}

		Machine.setPC((short)0); /* where code to be executed begins */
		Machine.setMSP(this.writer.getCurrentProgramCounter()); /* where memory stack begins */
		Machine.setMLP((short)(Machine.memorySize - 1)); /* */
	}
	
	// --- Program ---

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
	
	// --- Statements ---
	
	@Override
	public void visit(IfStmt visitable) {
		// DO NOT CALL SUPER
		// TODO: Handle conditional AND and OR!
		
		// Leaves the result on top of the stack.
		visitable.getCondition().accept(this);
		
		// If false, we want to jump to the end of the IF-block or inside the ELSE-block.
		AddressPatch patchIf = this.writer.writePatchableBranchIfFalse();
		
		for (Stmt trueStmt : visitable.getWhenTrue()) {
			trueStmt.accept(this);
		}

		// If there is no else, then "whenFalse" will be null.
		if (visitable.getWhenFalse() == null) {
			// Patch up the IF-block to jump here when false.
			this.writer.patchAddress(patchIf, this.writer.getCurrentProgramCounter());
		} else {
			AddressPatch pathToEnd = this.writer.writePatchableBranchAlways();
			this.writer.patchAddress(patchIf, this.writer.getCurrentProgramCounter());
			
			for (Stmt falseStmt : visitable.getWhenFalse()) {
				falseStmt.accept(this);
			}
			this.writer.patchAddress(pathToEnd, this.writer.getCurrentProgramCounter());
		}
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
	
	// --- Expressions ---
	
	@Override
	public void visit(AnonFuncExpn visitable) {
		// TODO
		super.visit(visitable);
	}
	
	@Override
	public void visit(ArithExpn visitable) {
		super.visit(visitable);
		
		String s = visitable.getOpSymbol();
		if (s.equals("+")) {
			this.writer.writeRawAssembly(Machine.ADD);
		} else if (s.equals("-")) {
			this.writer.writeRawAssembly(Machine.SUB);
		} else if (s.equals("*")) {
			this.writer.writeRawAssembly(Machine.MUL);
		} else if (s.equals("/")) {
			this.writer.writeRawAssembly(Machine.DIV);
		} else { 
			System.out.println("WARNING: Encountered bad symbol.");
		}
	}
	
	@Override
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);		
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
	}
	
	@Override
	public void visit(BoolExpn visitable) {
		// Do conditional evaluation!
		
		// Evaluate the first expression.
		visitable.getFirstExpression().accept(this);
		
		if (visitable.getOpSymbol().equals("|")) {
			// If we see "true", we don't want to evaluate the second expression.
			AddressPatch toNextExpnPatch = this.writer.writePatchableBranchIfFalse();
			
			// So, we have seen "true", lets push "true" back on the track and jump to the end. 
			this.writer.writeRawAssembly(Machine.PUSH, Machine.MACHINE_TRUE);
			AddressPatch toEndPatch = this.writer.writePatchableBranchAlways();
			
			// So, we have seen "false", lets evaluate the next expression and leave that.
			this.writer.patchAddress(toNextExpnPatch);
			visitable.getSecondExpression().accept(this);
			
			// Now, we are done evaluating the second expression.
			this.writer.patchAddress(toEndPatch);	
			
		} else if (visitable.getOpSymbol().equals("&")) {
			// If we see "false", we don't want to evaluate the second expression.
			AddressPatch toNextExpnPatch = this.writer.writePatchableBranchIfFalse();
			
			// So, we have seen "true", lets evaluate the next expression.
			visitable.getSecondExpression().accept(this);
			AddressPatch toEndPatch = this.writer.writePatchableBranchAlways();
			
			// So, we have seen "false", lets just push that and get out!
			this.writer.patchAddress(toNextExpnPatch);
			this.writer.writeRawAssembly(Machine.PUSH, Machine.MACHINE_FALSE);
			
			// Now, we are done evaluating the second expression.
			this.writer.patchAddress(toEndPatch);		
			
		} else {
			System.out.println("WARNING: Encountered bad symbol.");
		}
	}
	
	@Override
	public void visit(IntConstExpn visitable) {
		this.writer.writeRawAssembly(Machine.PUSH, visitable.getValue());
	}
	
	@Override
	public void visit(SkipConstExpn visitable) {
		// Handled by PutStmt
	}
	
	@Override
	public void visit(TextConstExpn visitable) {
		// Handled by PutStmt
	}
	
	
}

package compiler488.codegen;

import java.util.LinkedList;

import compiler488.ast.expn.BoolConstExpn;
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

	public LinkedList<Quadruple> quadruples = new LinkedList<Quadruple>();

	public CodeGenVisitor() {
	}

	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		program.accept(this);

		short programCount = 0;
		for (Quadruple quad : quadruples) {
			programCount = quad.generateCode(programCount);
			System.out.println(quad.toString());
		}

		Machine.setPC((short) 0); /* where code to be executed begins */
		Machine.setMSP(programCount); /* where memory stack begins */
		Machine.setMLP((short) (Machine.memorySize - 1)); /* */
	}

	@Override
	public void visit(Program visitable) {
		if (DEBUGGING) {
			quadruples.add(new RawQuadruple(Machine.TRON));
		}
		super.visit(visitable);
		if (DEBUGGING) {
			quadruples.add(new RawQuadruple(Machine.TROFF));
		}
		quadruples.add(new RawQuadruple(Machine.HALT));
	}
	
	public void visit(IfStmt visitable) {
		// DO NOT CALL SUPER
		
		// Leaves the result on top of the stack.
		visitable.getCondition().accept(this);
		
		quadruples.add()
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
	
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);
		
		quadruples.add(new RawQuadruple(Machine.PUSH, visitable.getValue() ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE));
	}

	@Override
	public void visit(PutStmt visitable) {
		super.visit(visitable);
		
		quadruples.add(new RawQuadruple(Machine.PUSH, 'i'));
		quadruples.add(new RawQuadruple(Machine.PUSH, 'h'));
		quadruples.add(new RawQuadruple(Machine.PRINTC));
		quadruples.add(new RawQuadruple(Machine.PRINTC));
	}
}

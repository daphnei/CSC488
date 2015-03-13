package compiler488.codegen;

import java.util.LinkedList;

import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.Stmt;
import compiler488.runtime.ExecutionException;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.semantics.NodeVisitor;

public class CodeGenVisitor extends NodeVisitor {
	
	public LinkedList<Quadruple> quadruples = new LinkedList<Quadruple>();
	
	public CodeGenVisitor() {
		
	}
	
	public void generateCode(Program program) throws MemoryAddressException, ExecutionException {
		program.accept(this);
		
		short programCount = 0;
		for (Quadruple quad : quadruples) {
			programCount = quad.generateCode(programCount);
		}
		
		Machine.setPC((short)0);	        /* where code to be executed begins */
		Machine.setMSP(programCount);   	/* where memory stack begins */
		Machine.setMLP((short)(Machine.memorySize - 1)); /* */
	}
	
	@Override
	public void visit(Program visitable) {		
		super.visit(visitable);
		quadruples.add(new RawQuadruple(Machine.HALT));
	}
}

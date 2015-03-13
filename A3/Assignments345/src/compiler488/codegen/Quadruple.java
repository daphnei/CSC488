package compiler488.codegen;

import compiler488.runtime.ExecutionException;
import compiler488.runtime.MemoryAddressException;

public abstract class Quadruple {
	
	private Short codeLine = null;
	
	/**
	 * Generates machine code in memory at the given program counter.
	 * @param programCounter The starting position in memory to generate code.
	 * @return The address of the next empty line of code in memory.
	 */
	public short generateCode(short programCounter) throws MemoryAddressException, ExecutionException {
		this.codeLine = programCounter;
		return programCounter;
	}
	
	public short getCodeLine() {
		return this.codeLine;
	}
	
	public boolean hasCodeBeenGenerated() {
		return this.codeLine == null;
	}
}

package compiler488.codegen;

import compiler488.runtime.ExecutionException;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;

/**
 * Represents a raw quadruple that directly corresponds to a machine instruction.
 * @author g2robint
 */
public class RawQuadruple extends Quadruple {
	
	short operation;
	Short argument;
	
	public RawQuadruple(short operation) {
		this.operation = operation;
		this.argument = null;
	}
	
	public RawQuadruple(short operation, char argument) {
		this(operation);
		this.argument = (short)argument;
	}
	
	public RawQuadruple(short operation, int argument) {
		this(operation);
		this.argument = (short)argument;
	}
	
	public RawQuadruple(short operation, short argument) {
		this(operation);
		this.argument = argument;
	}

	@Override
	public short generateCode(short programCounter) throws MemoryAddressException, ExecutionException {
		super.generateCode(programCounter);
		
		Machine.writeMemory(programCounter, this.operation);
		programCounter++;
		
		if (this.argument != null) {
			Machine.writeMemory(programCounter, this.argument);
			programCounter++;			
		}
		return programCounter;
	}
	
	@Override
	public String toString() {
		return String.format("(RAW, %s, %d)", Machine.instructionNames[this.operation], this.argument == null ? 0 : this.argument);
	}
}

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
	Short arguement;
	
	public RawQuadruple(short operation) {
		this.operation = operation;
		this.arguement = null;
	}
	
	public RawQuadruple(short operation, char argument) {
		this(operation);
		this.operation = operation;
	}
	
	public RawQuadruple(short operation, int argument) {
		this(operation);
		this.operation = operation;
	}
	
	public RawQuadruple(short operation, short argument) {
		this(operation);
		this.operation = operation;
	}

	@Override
	public short generateCode(short programCounter) throws MemoryAddressException, ExecutionException {
		super.generateCode(programCounter);
		
		Machine.writeMemory(programCounter, this.operation);
		programCounter++;
		
		if (this.arguement != null) {
			Machine.writeMemory(programCounter, this.arguement);
			programCounter++;			
		}
		return programCounter;
	}
}

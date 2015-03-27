package compiler488.codegen;

import java.util.LinkedList;
import java.util.List;
import compiler488.runtime.Machine;

public class CodeWriter {
	
	private short programCounter = 0;
	private List<String> debugRecord = new LinkedList<String>();
	
	public CodeWriter() {
		
	}
	
	public short getCurrentProgramLength() {
		return this.programCounter;
	}
	
	public void printWrittenCode() {
		for (String str : this.debugRecord) {
			System.out.println(str);
		}
	}
	
	
	public short writeRawAssembly(short operation) {
		return this.writeRawAssembly(operation, null);
	}
	
	public short writeRawAssembly(short operation, char argument) {
		return this.writeRawAssembly(operation, (int)argument);
	}
	
	// TODO: If what you want to write doesn't fall neatly into this category, create your own "writeBranch" method or something.
	public short writeRawAssembly(short operation, Integer argument) {
		short writePosition = this.programCounter;
		
		Machine.writeMemory(this.programCounter, operation);
		this.programCounter++;		
		if (argument != null) {
			Machine.writeMemory(this.programCounter, (short)(int)argument);
			this.programCounter++;			
		}
		
		this.record(writePosition, "RAW", Machine.instructionNames[operation], argument == null ? 0 : argument);
		return writePosition;
	}

	// TODO: Add overloads for the method with variable parameters.
	private void record(short memoryAddr, String writeType, Object arg1, Object arg2) {
		String debug = String.format("%d : (%s, %s, %s)", memoryAddr, writeType, arg1.toString(), arg2.toString());
		this.debugRecord.add(debug);
	}
}

package compiler488.codegen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import compiler488.runtime.Machine;

public class CodeWriter {
	
	private short programCounter = 0;
	private List<String> debugRecord = new ArrayList<String>();
	private List<AddressPatch> requiredPatches = new LinkedList<AddressPatch>();
	
	public CodeWriter() {
		
	}
	
	public short getCurrentProgramCounter() {
		return this.programCounter;
	}
	
	public boolean isCompletelyPatched() {
		return this.requiredPatches.isEmpty();
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
	
	public short writeRawAssembly(short operation, short argument) {
		return this.writeRawAssembly(operation, (int)argument);
	}
	
	/**
	 * Writes an assembly instruction to memory.
	 * @param operation The operation to write.
	 * @param argument The argument (null if none) with the operation.
	 * @return The program counter after the lines have been written.
	 */
	public short writeRawAssembly(short operation, Integer argument) {
		short writePosition = this.programCounter;
		Machine.writeMemory(this.programCounter, operation);
		this.programCounter++;		
		if (argument != null) {
			Machine.writeMemory(this.programCounter, (short)(int)argument);
			this.programCounter++;			
		}
		
		this.record(writePosition, operation, argument, null);
		return this.programCounter;
	}
	
	public AddressPatch writePatchableBranchIfFalse() {
		return this.writePatchableBranch(Machine.BF);
	}
	
	public short writeBranchIfFalse(short addressToBranch) {
		return this.writeBranch(Machine.BF, addressToBranch);
	}
	
	public AddressPatch writePatchableBranchAlways() {
		return this.writePatchableBranch(Machine.BR);
	}
	
	public short writeBranchAlways(short addressToBranch) {
		return this.writeBranch(Machine.BR, addressToBranch);
	}
	
	private AddressPatch writePatchableBranch(short branchOperation) {
		short originalMemory = this.programCounter;		
		this.writeBranch(branchOperation, this.programCounter);
		
		int debugRecordIndex = this.debugRecord.size() - 2;
		this.record(debugRecordIndex, originalMemory, branchOperation, "PATCH-ME", null);
		
		AddressPatch patch = new AddressPatch(originalMemory + 1, this.programCounter, debugRecordIndex);
		this.requiredPatches.add(patch);
		
		return patch; 
	}
	
	private short writeBranch(short branchOperation, short addressToBranch) {	
		this.writeRawAssembly(Machine.PUSH, addressToBranch);
		this.writeRawAssembly(branchOperation);		
		return this.programCounter;
	}
	
	public void patchAddress(AddressPatch needingPatch, short newAddress) {
		if (this.requiredPatches.contains(needingPatch)) {
			Machine.writeMemory(needingPatch.addressToBePatched, newAddress);
			this.requiredPatches.remove(needingPatch);
			this.record("PATCHED : " + needingPatch.addressToBePatched + " is set to " + newAddress);
		} else {
			System.out.println("ERROR: Trying to patch address that has already been patched!");
		}
	}
	
	private void record(short memoryAddr, short machineOp, Object arg1, Object arg2) {
		this.record(this.debugRecord.size(), memoryAddr, machineOp, arg1, arg2);
	}
	
	private void record(int debugIndex, short memoryAddr, short machineOp, Object arg1, Object arg2) {
		String debug = String.format(
				"%d : (%s, %s, %s)",
				memoryAddr,
				Machine.instructionNames[machineOp],
				arg1 != null ? arg1.toString() : "",
				arg2 != null ? arg2.toString() : "");
		
		if (debugIndex >= this.debugRecord.size()) {
			this.debugRecord.add(debug);
		} else {
			this.debugRecord.set(debugIndex, debug);
		}
	}
	
	private void record(String text) {
		this.debugRecord.add(text);
	}
}

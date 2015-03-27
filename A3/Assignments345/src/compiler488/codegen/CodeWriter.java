package compiler488.codegen;

import java.util.LinkedList;
import java.util.List;
import compiler488.runtime.Machine;

public class CodeWriter {
	
	private short programCounter = 0;
	private List<String> debugRecord = new LinkedList<String>();
	private List<AddressPatch> requiredPatches = new LinkedList<AddressPatch>();
	
	public CodeWriter() {
		
	}
	
	public short getCurrentProgramLength() {
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
		short addr = this.writeBranch(branchOperation, this.programCounter);
		AddressPatch patch = new AddressPatch(addr + 1, addr);
		this.requiredPatches.add(patch);
		return patch; 
	}
	
	private short writeBranch(short branchOperation, short addressToBranch) {
		short writePosition = this.programCounter;
		
		this.writeRawAssembly(Machine.PUSH, addressToBranch);
		this.writeRawAssembly(branchOperation);
		
		return writePosition;
	}
	
	public void patchAddress(AddressPatch needingPatch, short newAddress) {
		if (this.requiredPatches.contains(needingPatch)) {
			Machine.writeMemory(needingPatch.addressToBePatched, newAddress);
			this.requiredPatches.remove(needingPatch);
		} else {
			System.out.println("ERROR: Trying to patch address that has already been patched!");
		}
	}
	
	// TODO: Add overloads for the method with variable parameters.
	private void record(short memoryAddr, String writeType, Object arg1, Object arg2) {
		String debug = String.format("%d : (%s, %s, %s)", memoryAddr, writeType, arg1.toString(), arg2.toString());
		this.debugRecord.add(debug);
	}
	
	private void recordNote(String note) {
		this.debugRecord.add(note);
	}
}

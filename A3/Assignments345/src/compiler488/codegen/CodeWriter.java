package compiler488.codegen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import compiler488.runtime.Machine;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

public class CodeWriter {
	
	private short programCounter = 0;
	private List<String> debugRecord = new ArrayList<String>();
	private List<AddressPatch> requiredPatches = new LinkedList<AddressPatch>();
	private SymbolTable symbolTable;
	
	public CodeWriter(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
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
	
	// --- Raw Assembly ---
	
	public short writeRawAssembly(short operation) {
		return this.internalWriteAssembly(operation, null, null);
	}
	
	public short writeRawAssembly(short operation, char argument) {
		return this.internalWriteAssembly(operation, (int)argument, null);
	}
	
	public short writeRawAssembly(short operation, short argument) {
		return this.internalWriteAssembly(operation, (int)argument, null);
	}
	
	public short writeRawAssembly(short operation, Integer argument) {
		return this.internalWriteAssembly(operation, (int)argument, null);
	}
	
	public short writeRawAssembly(short operation, short argument1, int argument2) {
		return this.internalWriteAssembly(operation, (int) argument1, (int) argument2);
	}

	/**
	 * Writes an assembly instruction to memory.
	 * @param operation The operation to write.
	 * @param argument The argument (null if none) with the operation.
	 * @return The program counter after the lines have been written.
	 */
	private short internalWriteAssembly(short operation, Integer argument1, Integer argument2) {
		short writePosition = this.programCounter;
		Machine.writeMemory(this.programCounter, operation);
		this.programCounter++;		
		
		// Check if there is a first argument.
		if (argument1 != null) {
			Machine.writeMemory(this.programCounter, (short)(int)argument1);
			this.programCounter++;	
			
			// If there was a first argument, there might also be a second arguments.
			if (argument2 != null) {
				Machine.writeMemory(this.programCounter, (short)(int)argument2);
				this.programCounter++;			
			}
		}
		
		this.record(writePosition, operation, argument1, argument2);
		return this.programCounter;
	}
	
	// --- Branch ---
	
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
		
		int debugRecordIndex = this.debugRecord.size() - 3; // HACK: Be careful about this!
		this.record(debugRecordIndex, originalMemory, Machine.PUSH, "PATCHME", null);
		
		AddressPatch patch = new AddressPatch(originalMemory + 1, this.programCounter, debugRecordIndex);
		this.requiredPatches.add(patch);
		
		return patch; 
	}
	
	private short writeBranch(short branchOperation, short addressToBranch) {
		this.record("<BRANCH>");
		this.writeRawAssembly(Machine.PUSH, addressToBranch);
		this.writeRawAssembly(branchOperation);
		this.record("</BRANCH>");
		return this.programCounter;
	}
	
	// --- Unsupported Operations ---
	
	public short writeNot() {
		this.record("<NOT>");
		this.writeRawAssembly(Machine.PUSH, Machine.MACHINE_FALSE);
		this.writeRawAssembly(Machine.EQ);
		this.record("</NOT>");
		return this.programCounter;
	}
	
	public AddressPatch writePatchablePush() {
		short originalMemory = this.programCounter;		
		this.writeRawAssembly(Machine.PUSH, this.programCounter);
		
		int debugRecordIndex = this.debugRecord.size() - 1; // HACK: Be careful about this!
		this.record(debugRecordIndex, originalMemory, Machine.PUSH, "PATCHME", null);
		
		AddressPatch patch = new AddressPatch(originalMemory + 1, this.programCounter, debugRecordIndex);
		this.requiredPatches.add(patch);
		return patch;
	}
	
	// --- Helpers ---
	
	public void writeBeginDebug() {
		this.writeRawAssembly(Machine.TRON);
	}
	
	public void writeEndDebug() {
		this.writeRawAssembly(Machine.TROFF);
	}
	
	/**
	 * Write the specified string out to standard output.
	 * @param string
	 */
	public void writeString(String string) {
		for(char c : string.toCharArray()) {
			this.writeRawAssembly(Machine.PUSH, c);
			this.writeRawAssembly(Machine.PRINTC);
		}
	}
	
	/**
	 * Before this method is entered, it is assumed that an array subscript has 
	 * been pushed on to the stack. 
	 * 
	 * This method generates code to add the specified offset to the subscripts, and
	 * then check to make sure the result is within the bounds of the array.
	 * 
	 * @param offset the offset for the dimension of the array index.
	 * @param length the number of array elements in the dimension being indexed.
	 * @param lineNumber This will be used in case of error.
	 */
	public void writeSubscriptOffsetAndBoundsCheck(int offset, int length, int lineNumber) {
		// First add the offset to the subscript value already at the top of the stack. 
		this.writeRawAssembly(Machine.PUSH, offset);
		this.writeRawAssembly(Machine.ADD);
		
		// Check if the resulting index is too small.
		this.writeRawAssembly(Machine.DUP);
		this.writeRawAssembly(Machine.PUSH, 1);
		this.writeRawAssembly(Machine.LT);

		// Check if the resulting index is too large.
		this.writeRawAssembly(Machine.DUP);
		this.writeRawAssembly(Machine.PUSH, length);
		this.writeRawAssembly(Machine.LT);
		
		// Check if either of these cases occured.
		this.writeRawAssembly(Machine.OR);
		
		AddressPatch patchIf = this.writePatchableBranchIfFalse();
		
		// If either case occurred, the print an error and exit.
		this.writeString("Line ");
		this.writeRawAssembly(Machine.PRINTI, lineNumber);
		this.writeString(": ArrayOutOfBoundsException. Halting execution.");
		this.writeRawAssembly(Machine.HALT);
		
		this.patchAddress(patchIf, this.programCounter);
	}
	
	public void writeSymbolAddress(String symbolIdentifier) {
		Symbol symbol = this.symbolTable.retrieveSymbol(symbolIdentifier);
		this.writeRawAssembly(Machine.ADDR, symbol.getLexicalLevel(this.symbolTable), symbol.getOffset());
	}
	
	public void patchAddress(AddressPatch needingPatch) {
		this.patchAddress(needingPatch, this.programCounter);
	}
	
	public void patchAddress(AddressPatch needingPatch, short newAddress) {
		if (this.requiredPatches.contains(needingPatch)) {
			Machine.writeMemory(needingPatch.addressToBePatched, newAddress);
			this.requiredPatches.remove(needingPatch);
			this.record("PATCH >> " + needingPatch.addressToBePatched + " is set to " + newAddress);
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
	
	public static void printStackTrace() {
		for(StackTraceElement s : Thread.currentThread().getStackTrace()) {
			System.out.println(s.toString());
		}
	}
}

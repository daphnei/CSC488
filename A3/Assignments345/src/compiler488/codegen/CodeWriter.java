package compiler488.codegen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import compiler488.runtime.Machine;
import compiler488.semantics.types.RoutineSemType;
import compiler488.symbol.SymScope;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;

/**
 * All writes to the machine should go through this class. It allows us to build debug information
 * about the program as code is generated, and provides an interface for common write operations.
 * 
 * @author Adam, Daphne
 */
public class CodeWriter {

	/**
	 * The current value of the program counter.
	 */
	private short programCounter = 0;
	
	/**
	 * A utility to keep track of all the code that has been written to the machine so far.
	 * We used this for debugging.
	 */
	private List<String> debugRecord = new ArrayList<String>();
	
	/**
	 * A list of the addresses that are part of branch statements, but we don't know exactly 
	 * where to branch yet. By the time the compiler is finished, all of these addresses 
	 * should be patched, and this list should be empty.
	 */
	private List<AddressPatch> requiredPatches = new LinkedList<AddressPatch>();

	/**
	 * Nothing much to see here.
	 */
	public CodeWriter() {
	}

	/**
	 * @return the current value of the program counter.
	 */
	public short getProgramCounter() {
		return this.programCounter;
	}

	/**
	 * @return true if all branch addresses have been patched, false otherwise.
	 */
	public boolean isCompletelyPatched() {
		return this.requiredPatches.isEmpty();
	}
	
	/**
	 * Print out a record of all of the code that has been written to the machine so far.
	 */
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
		return this.internalWriteAssembly(operation, (int) argument, null);
	}

	public short writeRawAssembly(short operation, short argument) {
		return this.internalWriteAssembly(operation, (int) argument, null);
	}

	public short writeRawAssembly(short operation, Integer argument) {
		return this.internalWriteAssembly(operation, (int) argument, null);
	}

	public short writeRawAssembly(short operation, short argument1, int argument2) {
		return this.internalWriteAssembly(operation, (int) argument1, (int) argument2);
	}

	/**
	 * Writes an assembly instruction to memory.
	 * 
	 * @param operation
	 *            The operation to write.
	 * @param argument
	 *            The argument (null if none) with the operation.
	 * @return The program counter after the lines have been written.
	 */
	private short internalWriteAssembly(short operation, Integer argument1, Integer argument2) {
		short writePosition = this.programCounter;
		Machine.writeMemory(this.programCounter, operation);
		this.programCounter++;

		// Check if there is a first argument.
		if (argument1 != null) {
			Machine.writeMemory(this.programCounter, (short) (int) argument1);
			this.programCounter++;

			// If there was a first argument, there might also be a second arguments.
			if (argument2 != null) {
				Machine.writeMemory(this.programCounter, (short) (int) argument2);
				this.programCounter++;
			}
		}

		this.record(writePosition, operation, argument1, argument2);
		return this.programCounter;
	}

	// --- Branch ---

	/**
	 * Write a conditional branch instruction
	 * Returns an AddressPatch object which you MUST eventually use to patch this branch statement.
	 * Failing to do so will generate an error.
	 * @return the address that needs to be patched.
	 */
	public AddressPatch writePatchableBranchIfFalse() {
		return this.writePatchableBranch(Machine.BF);
	}

	/**
	 * Write a conditional branch instruction where we know exactly what address to branch to  
	 * (no patching is needed.)
	 * @param addressToBranch The address to branch to.
	 * @return the program counter following this instruction's addition
	 */
	public short writeBranchIfFalse(short addressToBranch) {
		return this.writeBranch(Machine.BF, addressToBranch);
	}

	/**
	 * Write an unconditional branch instruction.
	 * @return an AddressPatch object which you MUST eventually use to patch this branch statement.
	 * Failing to do so will generate an error.
	 */
	public AddressPatch writePatchableBranchAlways() {
		return this.writePatchableBranch(Machine.BR);
	}

	/**
	 * Write an unconditional branch instruction.
	 * @param addressToBranch The address to branch to.
	 * @return
	 */
	public short writeBranchAlways(short addressToBranch) {
		return this.writeBranch(Machine.BR, addressToBranch);
	}

	/**
	 * Write a branch instruction that needs to be patched.
	 * 
	 * Returns an AddressPatch object which you MUST eventually use to patch this branch statement.
	 * Failing to do so will generate an error.
	 * 
	 * @param branchOperation
	 *            The type of branch operation.
	 * @return An AddressPatch which stores information about the memory region that needs patching.
	 */
	private AddressPatch writePatchableBranch(short branchOperation) {
		short originalMemory = this.programCounter;
		this.writeBranch(branchOperation, this.programCounter);

		int debugRecordIndex = this.debugRecord.size() - 3; // HACK: Be careful about this!
		this.record(debugRecordIndex, originalMemory, Machine.PUSH, "PATCHME", null);

		AddressPatch patch = new AddressPatch(originalMemory + 1, this.programCounter, debugRecordIndex);
		this.requiredPatches.add(patch);

		return patch;
	}

	/**
	 * Write a branch instruction, and record that we are doing so.
	 * @param branchOperation Whether to use BR or BF
	 * @param addressToBranch The address to branch to.
	 * @return
	 */
	private short writeBranch(short branchOperation, short addressToBranch) {
		this.record("<BRANCH>");
		this.writeRawAssembly(Machine.PUSH, addressToBranch);
		this.writeRawAssembly(branchOperation);
		this.record("</BRANCH>");
		return this.programCounter;
	}

	// --- Unsupported Operations ---

	/**
	 * Write the instruction to perform the NOT expression/
	 * @return the position of the program counter after this instruction has been written.
	 */
	public short writeNot() {
		this.record("<NOT>");
		this.writeRawAssembly(Machine.PUSH, Machine.MACHINE_FALSE);
		this.writeRawAssembly(Machine.EQ);
		this.record("</NOT>");
		return this.programCounter;
	}

	/**
	 * Write a push instruction where the argument to push needs to be patched.
	 * @return The patch created.
	 */
	public AddressPatch writePatchablePush() {
		short originalMemory = this.programCounter;
		this.writeRawAssembly(Machine.PUSH, this.programCounter);

		int debugRecordIndex = this.debugRecord.size() - 1; // HACK: Be careful about this!
		this.record(debugRecordIndex, originalMemory, Machine.PUSH, "PATCHME", null);

		AddressPatch patch = new AddressPatch(originalMemory + 1, this.programCounter, debugRecordIndex);
		this.requiredPatches.add(patch);
		return patch;
	}

	// --- Function Setup / Tear-down

	/**
	 * Use this method to write a control block. The AddressPatch must be patched with
	 * the return value.
	 * @return The patch created.
	 */
	public AddressPatch writeControlBlock(short lexicalLevelForActivationRecord) {
		// Push space for the return value.
		this.writeRawAssembly(Machine.PUSH, 0);

		// Push the return address.
		AddressPatch returnAddressPatch = this.writePatchablePush();

		// Push the value of the current LL display for this routine onto the stack.
		this.writeRawAssembly(Machine.ADDR, lexicalLevelForActivationRecord, 0);

		return returnAddressPatch;
	}

	/**
	 * Call after setting up the parameters when calling a routine. For not, we've decided not to use this.
	 * @param routine An object representing the routine we are running.
	 * @param afterFunctionCallPatch A patch from the Before method we need here.
	 */
	public void writeRoutineCallAfter(RoutineSemType routine, AddressPatch returnAddressPatch) {

	}
	
	/**
	 * This function sets up a routine after being declared.
	 * @param RoutineSemType object storing information about the parameters to the routine.
	 */
	public AddressPatch writeRoutineDeclareSetup(RoutineSemType routine) {		
		// The following values are pushed on from the caller.
		// - Return value space
		// - The line of code to return to
		// - The value in the display
		// - The parameters
		
		// Set the display register.
		short controlBlockAndParameters = (short)(CodeGenVisitor.CONTROL_BLOCK_SIZE + routine.getNumParameters());
		this.writeRawAssembly(Machine.PUSHMT);
		this.writeRawAssembly(Machine.PUSH, controlBlockAndParameters);
		this.writeRawAssembly(Machine.SUB);
		this.writeRawAssembly(Machine.SETD, routine.getLexicalLevel());
		
		// Push space for local variables on stack.	
		this.writeRawAssembly(Machine.PUSH, Machine.UNDEFINED); // Dummy value.
		AddressPatch localMinusParamsSizePatch = this.writePatchablePush();
		this.writeRawAssembly(Machine.DUPN);
		
		return localMinusParamsSizePatch;
	}
	
	/**
	 * This function tears down a routine once it has been executed/
	 * @param routine The object storing info about the routine.
	 * @param localVariableSpace How much space was being used by variables declared in this routine's scope.
	 * @param localMinusParamsSizePatch The patch corresponding to the first address of the routine following the activation record.
	 */
	public void writeRoutineDeclareTeardown(RoutineSemType routine, int localVariableSpace, AddressPatch localMinusParamsSizePatch) {
		// Delete the local variables.
		this.writeRawAssembly(Machine.PUSH, localVariableSpace); // Includes parameters.
		this.writeRawAssembly(Machine.POPN);
		
		// Set the display register back; the value on the stack is the previous value for this register.
		this.writeRawAssembly(Machine.SETD, routine.getLexicalLevel());

		// Jump to back to where this method was called.
		this.writeRawAssembly(Machine.BR);
		
		this.patchAddress(localMinusParamsSizePatch, (short)(localVariableSpace - routine.getNumParameters()));
	}
	
	// --- Helpers ---

	/**
	 * Write the instruction to start the machine doing debug.
	 */
	public void writeBeginDebug() {
		this.writeRawAssembly(Machine.TRON);
	}
	
	/**
	 * Write the instruction to stop the machine doing debug.
	 */
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
		
		// Push a couple more copies of this index onto the stack, for use with error checking.
		this.writeRawAssembly(Machine.DUP);
		this.writeRawAssembly(Machine.DUP);
		
		// Check if the index is too small.
		this.writeRawAssembly(Machine.PUSH, 1);
		this.writeRawAssembly(Machine.LT);

		// Check if the resulting index is too large.
		this.writeRawAssembly(Machine.SWAP);
		this.writeRawAssembly(Machine.PUSH, length);
		this.writeRawAssembly(Machine.SWAP);
		this.writeRawAssembly(Machine.LT);
		
		// Check if either of these cases occurred.
		this.writeRawAssembly(Machine.OR);
		
		AddressPatch patchIf = this.writePatchableBranchIfFalse();
		
		// If either case occurred, the print an error and exit.
		this.writeErrorThenHalt(lineNumber, "ArrayOutOfBoundsException.");
		
		this.patchAddress(patchIf, this.programCounter);
	}

	/**
	 * Write a runtime error. A good use case for this is bad array bounds.
	 * @param lineNumber The line number on which the error occured.
 	 * @param message The type of problem that occured.
	 */
	public void writeErrorThenHalt(int lineNumber, String message) {
		this.writeString("Line ");
		this.writeRawAssembly(Machine.PRINTI, lineNumber);
		this.writeString(": " + message + " Halting execution.");
		this.writeRawAssembly(Machine.HALT);
	}
	
	/**
	 * Write the address of the specified symbol in the symbol table.
	 * @param symbolIdentifier The name of the symbol.
	 * @param symbolTable A reference to the symbol table.
	 */
	public void writeSymbolAddress(String symbolIdentifier, SymbolTable symbolTable) {
		Symbol symbol = symbolTable.retrieveSymbol(symbolIdentifier);
		this.writeRawAssembly(Machine.ADDR, symbol.getLexicalLevel(symbolTable), symbol.getOffset());
	}
	
	// --- Patching ---

	/**
	 * Patch the provided address with the current value of the program counter.
	 * @param needingPatch
	 */
	public void patchAddress(AddressPatch needingPatch) {
		this.patchAddress(needingPatch, this.programCounter);
	}

	/**
	 * Patch the provided address with the specified position.
	 * @param needingPatch The patch that needs revising.
	 * @param newAddress The address to make the patch point to.
	 */
	public void patchAddress(AddressPatch needingPatch, short newAddress) {
		if (this.requiredPatches.contains(needingPatch)) {
			Machine.writeMemory(needingPatch.addressToBePatched, newAddress);
			this.requiredPatches.remove(needingPatch);
			this.record("PATCH >> " + needingPatch.addressToBePatched + " is set to " + newAddress);
		} else {
			System.out.println("ERROR: Trying to patch address that has already been patched!");
		}
	}
	
	/**
	 * Cause all of the patches in the specified scope to be patched with the specified address.
	 * This is a utility used to patch all of the returns inside a function or all of the exits
	 * inside a loop.
	 * @param scope A scope object.
	 * @param patchAddress The address to patch to.
	 */
	public void patchReturnsOrExits(SymScope scope, short patchAddress) {
		ArrayList<AddressPatch> exitsToBePatched = scope.getAllExitsToBePatched();
		for (AddressPatch patch : exitsToBePatched) {
			this.patchAddress(patch, patchAddress);
		}
	}
	
	// --- Debug Info ---

	/**
	 * Mark down that some instruction has been generated. This was used for debugging.
	 */
	private void record(short memoryAddr, short machineOp, Object arg1, Object arg2) {
		this.record(this.debugRecord.size(), memoryAddr, machineOp, arg1, arg2);
	}

	/**
	 * Mark down that some instruction has been generated. This was used for debugging.
	 */
	private void record(int debugIndex, short memoryAddr, short machineOp, Object arg1, Object arg2) {
		String debug = String.format("%d : (%s, %s, %s)", memoryAddr, Machine.instructionNames[machineOp], arg1 != null ? arg1.toString()
				: "", arg2 != null ? arg2.toString() : "");

		if (debugIndex >= this.debugRecord.size()) {
			this.debugRecord.add(debug);
		} else {
			this.debugRecord.set(debugIndex, debug);
		}
	}

	/**
	 * Mark down some text. This was used for debugging.
	 */
	private void record(String text) {
		this.debugRecord.add(text);
	}

	/**
	 * Print a record of the stack.
	 */
	public static void printStackTrace() {
		for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
			System.out.println(s.toString());
		}
	}
}

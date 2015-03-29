package compiler488.symbol;

import java.util.ArrayList;

import compiler488.codegen.AddressPatch;
import compiler488.codegen.CodeGenVisitor;
import compiler488.runtime.Machine;
import compiler488.semantics.types.RoutineSemType;

public class SymScope {
	private ScopeType scopeType;
	
	/**
	 * The lexical level corresponding to this scope.
	 * This is only used in code generation.
	 */
	private short lexicalLevel;
	
	/**
	 * The offset to the first address in this lexical level
	 * that has not been allocated yet.
	 */
	private short freeOffset = CodeGenVisitor.CONTROL_BLOCK_SIZE;
	
	private RoutineSemType associatedRoutine = null;
	
	private ArrayList<AddressPatch> returnsAndExitsToBePatched;
	
	public SymScope(ScopeType scopeType) {
		this(scopeType, Machine.UNDEFINED);
	}
	
	public SymScope(ScopeType scopeType, short lexicalLevel) {
		this.scopeType = scopeType;
		this.lexicalLevel = lexicalLevel;
		this.freeOffset = CodeGenVisitor.CONTROL_BLOCK_SIZE;
		this.returnsAndExitsToBePatched = new ArrayList<AddressPatch>();
	}
	
	public ScopeType getScopeType() {
		return this.scopeType;
	}
	
	public boolean isMajor() {
		return this.scopeType.isMajor();
	}
	
	public boolean isMinor() {
		return this.scopeType.isMinor();
	}
	
	/**
	 * Assigns space in the lexical level for a new variable. 
	 * Increments the free offset to mark the space now in use. 
	 * @param varSize The number of shorts the variable should take up in memory.
	 *                For an integer or a boolean, this will be 1. For an array, 
	 *                this will be larger.
	 * @return The offset to the first memory address of the variable.
	 */
	public int assignSpaceForNewVariable(int varSize) {
		if (this.scopeType.isMinor()) {
			throw new RuntimeException("ERROR: You should not be assigning space to a minor scope!");
		}
		int addressOfVar = this.freeOffset;
		this.freeOffset += varSize;
		return addressOfVar;
	}
	
	public void setRoutine(RoutineSemType routine) {
		this.associatedRoutine = routine;
	}
	
	public RoutineSemType getRoutine() {
		return this.associatedRoutine;
	}
	
	public short getOffset() {
		return this.freeOffset;
	}
	
	public short getSpaceAllocatedForVariables() {
		return (short)(this.freeOffset - CodeGenVisitor.CONTROL_BLOCK_SIZE);
	}
	
	public short getLexicalLevel() {
		return this.lexicalLevel;
	}
	
	/**
	 * Note down that there is an exit or return from this scope that
	 * needs to be patched.
	 * @param patch
	 */
	public void keepTrackOfAnExit(AddressPatch patch) {
		this.returnsAndExitsToBePatched.add(patch);
	}
	
	/**
	 * Returns a list of all of the branching statements that need to be
	 * patched with the first address outside of this scope.
	 * @return
	 */
	public ArrayList<AddressPatch> getAllExitsToBePatched() {
		return this.returnsAndExitsToBePatched;
	}
}

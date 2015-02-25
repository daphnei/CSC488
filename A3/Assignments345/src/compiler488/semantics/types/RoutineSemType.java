package compiler488.semantics.types;

import java.util.ArrayList;

/**
 * The semantic type for a function or procedure.
 * @author daphne
 *
 */
public class RoutineSemType extends SemType {
	/**
	 * The types of the routines parameters. Empty if there are no parameters.
	 */
	private ArrayList<PrimitiveSemType> parameters;
	
	/**
	 * The return type. Null if this is a procedure.
	 */
	private PrimitiveSemType returnType;

	/**
	 * This starts out as false and then gets set to true once a return statement is seen.
	 */
	private boolean seenReturnStatement;
	
	/**
	 * Create a function with parameters and a return value.
	 * @param parameters The parameters to the function.
	 * @param returnType The return type of the function.
	 */
	public RoutineSemType(PrimitiveSemType returnType) {
		this.returnType = returnType;
		this.parameters = new ArrayList<PrimitiveSemType>();
	}

	public ArrayList<PrimitiveSemType> getParameters() {
		return this.parameters;
	}
	
	public void addParameter(PrimitiveSemType param) {
		this.parameters.add(param);
	}

	public int getNumParameters() {
		return this.parameters.size();
	}
	
	public PrimitiveSemType getReturnType() {
		return this.returnType;
	}

	/**
	 * Indicate that a return statement has been seen for this routine.
	 */
	public void markReturnStatement() {
		this.seenReturnStatement = true;
	}
	
	/**
	 * Check whether a return statement has been seen for this routine.
	 */
	public boolean seenReturnStatement() {
		return this.seenReturnStatement;
	}
}

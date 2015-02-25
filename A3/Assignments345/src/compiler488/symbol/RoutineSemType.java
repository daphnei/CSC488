package compiler488.symbol;

import java.util.ArrayList;

public class RoutineSemType extends SemType {
	private ArrayList<PrimitiveSemType> parameters;
	private PrimitiveSemType returnType;

	/**
	 * This starts out as false and then gets set to true once a return statement is seen.
	 */
	private boolean seenReturnStatement;
	
	/**
	 * Create a routine with no return value.
	 * (ie a procedure) 
	 */
	public RoutineSemType() {
		this(null);
	}
	
	/**
	 * Create a function with parameters and a return value.
	 * @param parameters The parameters to the function.
	 * @param returnType The return type of the function.
	 */
	public RoutineSemType(PrimitiveSemType returnType) {
		this.returnType = returnType;
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
	
	public void markReturnStatement() {
		this.seenReturnStatement = true;
	}
	
	public boolean seenReturnStatement() {
		return this.seenReturnStatement;
	}
}

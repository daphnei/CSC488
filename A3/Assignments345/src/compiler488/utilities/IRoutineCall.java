package compiler488.utilities;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

/**
 * A common interface for the FunctionCallExpn and the ProcedureCallStmt
 * 
 * @author daphne
 */
public interface IRoutineCall {
	/**
	 * @return the arguments to the routine
	 */
	public ASTList<Expn> getArguments();
	
	/**
	 * @return the name of the routine
	 */
	public String getIdentifier();
}

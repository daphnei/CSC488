package compiler488.interfaces;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

/**
 * A common interface for the FunctionCallExpn and the ProcedureCallStmt
 * 
 * @author daphne
 */
public interface IRoutineCall extends IIdentifier {
	/**
	 * @return the arguments to the routine
	 */
	public ASTList<Expn> getArguments();
}

package compiler488.utilities;

/**
 * An element of the programming language that can be visited during the
 * traversal of the AST.
 * 
 * @author daphne
 *
 */
public interface IVisitableElement {
	public void accept(IVisitor visitor);
}
	
	

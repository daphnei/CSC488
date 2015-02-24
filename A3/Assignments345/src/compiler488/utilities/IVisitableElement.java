package compiler488.utilities;

import compiler488.ast.AST;

/**
 * An element of the programming language that can be visited during the
 * traversal of the AST.
 *
 * @author daphne
 *
 */
public interface IVisitableElement extends AST {
	public void accept(IVisitor visitor);
}



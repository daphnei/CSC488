package compiler488.ast;

import compiler488.interfaces.IVisitableElement;

/**
 * Any AST node that can be an argument in a PUT statement.
 *
 * <p>Don't confuse with concept with the printing of the AST itself.</p>
 */
public interface Printable extends AST, IVisitableElement {
}

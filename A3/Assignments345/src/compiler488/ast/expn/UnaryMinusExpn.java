package compiler488.ast.expn;

import compiler488.utilities.IVisitor;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand, int left, int right) {
        super(UnaryExpn.OP_MINUS, operand,left,right);
    }
}

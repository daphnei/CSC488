package compiler488.semantics.types;

import compiler488.ast.type.Type;

/**
 * The parent class for the primitive semantic types, integer and boolean.
 * 
 * @author Adam
 */
public abstract class PrimitiveSemType extends SemType {
    public abstract Type returnAST();
}

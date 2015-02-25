package compiler488.semantics.types;

/**
 * Semantic types are used for keeping track of the expected return
 * values of expressions, and the types of variables in the symbol table.
 * 
 * @author adam
 *
 */
public abstract class SemType {
	
	/**
	 * The default behavior is that two semantic types of the 
	 * same type should be considered the same.
	 */
	@Override
    public boolean equals(Object obj) {
		return this.getClass().equals(obj.getClass());
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
}

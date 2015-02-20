package compiler488.symbol;

/**
 * A scope for use in the symbol table. In the textbook, they just use a single
 * index for keeping track of the inner most scope, but because we also need to
 * keep track of whether each scope is Major or Minor, I figured the easiest way
 * to do this would be to maintain a list of objects that contain this information
 * for each scope index.
 *
 * @author daphne
 *
 */
public class SymTableScope {
	/**
	 * A major scope is a program, function or precedure.
	 * A minor scope is an if statement or loop.
	 * @author daphne
	 *
	 */
	public enum ScopeType {MAJOR, MINOR}

	/**
	 * Whether the scope is major or minor.
	 */
	private ScopeType scopeType;

	/**
	 * The index of this scope.
	 * It might be redundant to store this since the index should be equal to
	 * the scope's position in the scopes list of the symbol table.
	 */
	private int depthIndex;

	public SymTableScope(int depthIndex, ScopeType scopeType) {
		this.depthIndex = depthIndex;
		this.scopeType = scopeType;
	}
}

package compiler488.symbol;

public enum ScopeType {
	YIELD, PROGRAM, ROUTINE, LOOP, GENERIC;

	public boolean isMajor() {
		return this.equals(YIELD) || this.equals(PROGRAM) || this.equals(ROUTINE);
	}

	public boolean isMinor() {
		return this.equals(LOOP) || this.equals(GENERIC);
	}
}
package compiler488.symbol;

public abstract class SemType {
	public static final BooleanSemType BOOLEAN = new BooleanSemType();
	public static final IntegerSemType INTEGER = new IntegerSemType();
	
	@Override
    public boolean equals(Object obj) {
		return this.getClass().equals(obj.getClass());
	}
	
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
}
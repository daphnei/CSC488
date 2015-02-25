package compiler488.semantics.types;

public class ArraySemType extends SemType {

	private PrimitiveSemType baseType;
	private int dimensions;

	public ArraySemType(PrimitiveSemType baseType, int dimensions) {
		this.dimensions = dimensions;
		this.baseType = baseType;
	}
	
	public int getDimensions() {
		return this.dimensions;
	}
	
	public PrimitiveSemType getBaseType() {
		return this.baseType;
	}

	@Override
	public boolean equals(Object obj) {
		ArraySemType other = (ArraySemType) obj;
		return this.getClass().equals(obj.getClass()) && this.dimensions == other.dimensions && this.baseType.equals(other.baseType);
	}

	@Override
	public String toString() {
		return "arrays";
	}
}
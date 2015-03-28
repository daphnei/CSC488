package compiler488.semantics.types;

public class ArraySemType extends SemType {

	private PrimitiveSemType baseType;
	private int dimensions;

	//Keep track of the indices in each dimension
	private Integer lengthDim1 = null;
	private Integer lengthDim2 = null;
	private Integer offsetDim1 = null;
	private Integer offsetDim2 = null;
	
	/**
	 * The simple constructor used for semantic analysis.
	 * @param baseType
	 * @param dimensions
	 */
	public ArraySemType(PrimitiveSemType baseType, int dimensions) {
		this.dimensions = dimensions;
		this.baseType = baseType;
	}

	/**
	 * The more complicated constructor used for code generation.
	 * 
	 * @param baseType
	 * @param lowerBound1
	 * @param upperBound1
	 */
	public ArraySemType(PrimitiveSemType baseType, int lowerBound1, int upperBound1) {
		this.dimensions = 1;
		this.baseType = baseType;
		
		this.setLengthForDim1(lowerBound1, upperBound1);
	}
	
	public ArraySemType(PrimitiveSemType baseType,
						Integer lowerBound1, Integer upperBound1,
						Integer lowerBound2, Integer upperBound2) {
		
		this.baseType = baseType;
		
		this.setLengthForDim1(lowerBound1, upperBound1);

		if (lowerBound2 == null || upperBound2 == null) {
			// There is only one dimension.
			this.dimensions = 1;
		} else {
			// There are two dimensions.
			this.dimensions = 2;
			this.setLengthForDim2(lowerBound2, upperBound2);
		}
	}
	
	private void setLengthForDim1(int lowerBound, int upperBound) {
		this.lengthDim1 = upperBound - lowerBound;
		this.offsetDim1 = -lowerBound;
	}
	
	private void setLengthForDim2(int lowerBound, int upperBound) {
		this.lengthDim2 = upperBound - lowerBound;
		this.offsetDim2 = -lowerBound;
	}
	
	/**
	 * @return the number of memory addresses this array should take up
	 */
	public int getSize() {
		return this.lengthDim1 * this.lengthDim2;
	}
	
	public int getNumDimensions() {
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

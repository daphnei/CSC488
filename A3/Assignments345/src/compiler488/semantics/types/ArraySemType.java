package compiler488.semantics.types;

/**
 * An object used to store information about an Array
 * symbol in the symbol table.
 * 
 * @author daphne
 *
 */
public class ArraySemType extends SemType {
	/**
	 * Whether the values in this array are integers or booleans.
	 */
	private PrimitiveSemType baseType;
	
	/**
	 * How many dimensions the array has. This will either be 1 or 2.
	 */
	private int dimensions;

	/**
	 * The number of elements in each dimension.
	 */
	private Integer lengthDim1 = null;
	private Integer lengthDim2 = null;
	
	/**
	 * The offset of each dimension. For example, if the lower bound of
	 * the dimension is -7, then the offset will be 7.
	 */
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
	 * A more complicated constructor used for code generation, that takes in the
	 * info for a 1-dimensional array.
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
	
	/**
	 * A more complicated constructor used for code generation.
	 * 
	 * @param baseType
	 * @param lowerBound1
	 * @param upperBound1
	 * @param lowerBound2
	 * @param upperBound2
	 */
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
	
	/**
	 * Figure out the length of the 2nd array dimension based on the upper and lower bounds.
	 * @param lowerBound
	 * @param upperBound
	 */
	private void setLengthForDim1(int lowerBound, int upperBound) {
		this.lengthDim1 = upperBound - lowerBound + 1;
		this.offsetDim1 = 1-lowerBound;
	}
	
	/**
	 * Figure out the length of the 1st array dimension based on the upper and lower bounds.
	 * @param lowerBound
	 * @param upperBound
	 */
	private void setLengthForDim2(int lowerBound, int upperBound) {
		this.lengthDim2 = upperBound - lowerBound + 1;
		this.offsetDim2 = 1-lowerBound;
	}
	
	/**
	 * @return the number of memory addresses this array should take up
	 */
	public int getSize() {
		if (this.dimensions == 1)
			return this.lengthDim1;
		else
			return this.lengthDim1 * this.lengthDim2;
	}
	
	/**
	 * @return the number of dimensions, either 1 or 2.
	 */
	public int getNumDimensions() {
		return this.dimensions;
	}
	
	/**
	 * @return the base type
	 */
	public PrimitiveSemType getBaseType() {
		return this.baseType;
	}

	/**
	 * @return how many elements there are in the first dimension
	 */
	public Integer getLengthDim1() {
		return lengthDim1;
	}
	
	/**
	 * @return how many elements there are in the second dimension.
	 */
	public Integer getLengthDim2() {
		return lengthDim2;
	}

	/**
	 * @return the offset for the first dimension.
	 */
	public Integer getOffsetDim1() {
		return offsetDim1;
	}

	/**
	 * @return the offset for the second dimension.
	 */
	public Integer getOffsetDim2() {
		return offsetDim2;
	}
	

	@Override
	public boolean equals(Object obj) {
		ArraySemType other = (ArraySemType) obj;
		return this.getClass().equals(obj.getClass()) && this.dimensions == other.dimensions && this.baseType.equals(other.baseType);
	}

	@Override
	public String toString() {
		return "ArraySemType [baseType=" + baseType + ", lengthDim1="
				+ lengthDim1 + ", lengthDim2=" + lengthDim2 + ", offsetDim1="
				+ offsetDim1 + ", offsetDim2=" + offsetDim2 + "]";
	}

	
}

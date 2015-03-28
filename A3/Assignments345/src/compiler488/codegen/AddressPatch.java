package compiler488.codegen;

/**
 * This represents a branch that hasn't been patched.
 * @author g2robint
 */
public class AddressPatch {
	public short addressToBePatched;
	public short addressAfterOperation;
	public int debugRecordIndex;
	
	public AddressPatch(int addressToBePatched, int addressAfterOperation, int debugRecordIndex) {
		this.addressToBePatched = (short)addressToBePatched;
		this.debugRecordIndex = debugRecordIndex;
		this.addressAfterOperation = (short)addressAfterOperation;
	}
}

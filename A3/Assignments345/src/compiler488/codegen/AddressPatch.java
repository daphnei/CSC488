package compiler488.codegen;

/**
 * This represents a branch that hasn't been patched.
 * @author g2robint
 */
public class AddressPatch {
	public short addressToBePatched;
	public short beginningOfBranchOperationAddress;
	
	public AddressPatch(int addressToBePatched, int beginningOfBranchOperationAddress) {
		this.addressToBePatched = (short)addressToBePatched;
		this.beginningOfBranchOperationAddress = (short)beginningOfBranchOperationAddress;
	}
}

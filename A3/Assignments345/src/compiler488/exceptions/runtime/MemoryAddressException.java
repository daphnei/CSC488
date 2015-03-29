package compiler488.exceptions.runtime;

/** Exception subclass for reporting machine address errors
 * @author Danny House
 */
public class MemoryAddressException extends RuntimeException
{
	public  MemoryAddressException( String msg ) {
		super( msg ) ;
	}
}

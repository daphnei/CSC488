package compiler488.runtime;

/** Exception subclass for reporting machine run time  errors
 * @author Danny House
 */
public class ExecutionException extends RuntimeException
{
	public  ExecutionException( String msg )
	{
		super( msg ) ;
	}
}

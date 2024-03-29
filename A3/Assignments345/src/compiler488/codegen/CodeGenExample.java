package compiler488.codegen;

import compiler488.compiler.Main;
import compiler488.exceptions.runtime.ExecutionException;
import compiler488.exceptions.runtime.MemoryAddressException;
import compiler488.runtime.Machine;

/**      CodeGenerator.java
 *<pre>
 *  Code Generation Conventions
 *
 *  To simplify the course project, this code generator is
 *  designed to compile directly to pseudo machine memory
 *  which is available as the private array memory[]
 *
 *  It is assumed that the code generator places instructions
 *  in memory in locations
 *
 *      memory[ 0 .. startMSP - 1 ]
 *
 *  The code generator may also place instructions and/or
 *  constants in high memory at locations (though this may
 *  not be necessary)
 *      memory[ startMLP .. Machine.memorySize - 1 ]
 *
 *  During program exection the memory area
 *      memory[ startMSP .. startMLP - 1 ]
 *  is used as a dynamic stack for storing activation records
 *  and temporaries used during expression evaluation.
 *  A hardware exception (stack overflow) occurs if the pointer
 *  for this stack reaches the memory limit register (mlp).
 *
 *  The code generator is responsible for setting the global
 *  variables:
 *      startPC         initial value for program counter
 *      startMSP        initial value for msp
 *      startMLP        initial value for mlp
 * </pre>
 * @author  <B> PUT YOUR NAMES HERE </B>
 */

public class CodeGenExample
{

	/** initial value for memory stack pointer */
	private short startMSP;
	/** initial value for program counter */
	private short startPC;
	/** initial value for memory limit pointer */
	private short startMLP;

	/** flag for tracing code generation */
	private boolean traceCodeGen = Main.traceCodeGen ;

	/**
	 *  Constructor to initialize code generation
	 */
	public CodeGenExample()
	{
		// YOUR CONSTRUCTOR GOES HERE.
	}

	// Utility procedures used for code generation GO HERE.

	/**
	 *  Additional intialization for gode generation.
	 *  Called once at the start of code generation.
	 *  May be unnecesary if constructor does everything.
	 */

	/** Additional initialization for Code Generation (if required) */
	public void Initialize()
	{
		/********************************************************/
		/* Initialization code for the code generator GOES HERE */
		/* This procedure is called once before codeGeneration  */
		/*                                                      */
		/********************************************************/

		return;
	}


	/**
	 *  Perform any requred cleanup at the end of code generation.
	 *  Called once at the end of code generation.
	 *  @throws MemoryAddressException  from Machine.writeMemory
	 *  @throws ExecutionException      from Machine.writeMemory
	 */
	public void Finalize()
	throws MemoryAddressException, ExecutionException     // from Machine.writeMemory
	{
		/********************************************************/
		/* Finalization code for the code generator GOES HERE.  */
		/*                                                      */
		/* This procedure is called once at the end of code     */
		/* generation                                           */
		/********************************************************/

		//  REPLACE THIS CODE WITH YOUR OWN CODE
		//  THIS CODE generates a single HALT instruction
		//  as an example.
		Machine.setPC((short) 0 ) ;		/* where code to be executed begins */
		Machine.setMSP((short)  1 );   	/* where memory stack begins */
		Machine.setMLP((short) ( Machine.memorySize -1 ) );
		/* limit of stack */
		Machine.writeMemory((short)  0, Machine.PUSH);
		Machine.writeMemory((short)  1 , (short)'a');
		Machine.writeMemory((short)  2, Machine.PUSH);
		Machine.writeMemory((short)  3 , (short)'b');
		Machine.writeMemory((short)  4, Machine.PUSH);
		Machine.writeMemory((short)  5 , (short)'c');
		Machine.writeMemory((short)  6 , Machine.PRINTC);
		Machine.writeMemory((short)  7 , Machine.PRINTC);
		Machine.writeMemory((short)  8 , Machine.PRINTC);		
		Machine.writeMemory((short)  9 , Machine.HALT);
		
		

		return;
	}

	/** Procedure to implement code generation based on code generation
	 *  action number
	 * @param actionNumber  code generation action to perform
	 */
	public void generateCode( int actionNumber )
	{
		if( traceCodeGen )
		{
			//output the standard trace stream
			Main.traceStream.println("CodeGen: C" +  actionNumber );
		}

		/****************************************************************/
		/*  Code to implement the code generation actions GOES HERE     */
		/*  This dummy code generator just prints the actionNumber      */
		/*  In Assignment 5, you'll implement something more interesting */
		/*                                                               */
		/*  FEEL FREE TO ignore or replace this procedure                */
		/****************************************************************/

		System.out.println("Codegen: C" + actionNumber );
		return;
	}

	//  ADDITIONAL FUNCTIONS TO IMPLEMENT CODE GENERATION GO HERE

}

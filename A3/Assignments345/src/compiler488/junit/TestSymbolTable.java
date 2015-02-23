package compiler488.junit;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import compiler488.semantics.SemanticError;
import compiler488.symbol.Symbol;
import compiler488.symbol.SymbolTable;
import compiler488.ast.type.*;;

public class TestSymbolTable {
	private SymbolTable symbolTable;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Before
    public void setUp() {
		this.symbolTable = new SymbolTable();
		this.symbolTable.Initialize();
	}
	
	@After
    public void tearDown() throws Exception {
        this.symbolTable.Finalize();   
    }
	
	/**
	 * Test opening and closing scopes.
	 * @throws SemanticError
	 */
	@Test
	public void testOpenCloseScope() throws SemanticError {
		//Test before nay scope has been opened.
		assertEquals(this.symbolTable.getCurrentScope(), -1);
		
		this.symbolTable.openMajorScope();
		assertEquals(this.symbolTable.getCurrentScope(), 0);
		
		this.symbolTable.openMinorScope();
		assertEquals(this.symbolTable.getCurrentScope(), 1);
		
		this.symbolTable.closeCurrentScope();
		assertEquals(this.symbolTable.getCurrentScope(), 0);
	}

	/**
	 * Test closing more scopes than there are open.
	 * @throws SemanticError
	 */
	@Test
	public void testCloseScopeInvalid1() throws SemanticError {
		//Close a scope before any scope has been opened.
		this.thrown.expect(SemanticError.class);
		this.symbolTable.closeCurrentScope();
	}
	
	/**
	 * Test closing more scopes than there are open.
	 * @throws SemanticError
	 */
	@Test
	public void testCloseScopeInvalid2() throws SemanticError {
		this.symbolTable.openMinorScope();
		this.symbolTable.closeCurrentScope();
		
		this.thrown.expect(SemanticError.class);
		this.symbolTable.closeCurrentScope();
	}
	
	/**
	 * Test adding symbols within a single scope.
	 * @throws SemanticError
	 */
	@Test
	public void testAddSymbolsOneScope() throws SemanticError {
		this.symbolTable.openMajorScope();
	
		Symbol s = this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
		Symbol s2 = this.symbolTable.retrieveSymbol("foo");
		assertEquals(s, s2);
		
		s = this.symbolTable.addSymbolToCurScope("bar", new IntegerType());
		s2 = this.symbolTable.retrieveSymbol("bar");
		assertEquals(s, s2);
	}
	
	/**
	 * Test add symbol when no scope exists.
	 * @throws SemanticError
	 */
	@Test
	public void testAddSymbolsNoScope() throws SemanticError {
		this.symbolTable.openMajorScope();
		this.symbolTable.closeCurrentScope();
		
		this.thrown.expect(SemanticError.class);
		this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
	}
	
	/**
	 * Test retrieving a symbol when there is no open scope.
	 * @throws SemanticError
	 */
	@Test
	public void testRetrieveSymbolsNoScope() throws SemanticError {
		this.symbolTable.openMajorScope();
		this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
		this.symbolTable.closeCurrentScope();
		
		this.thrown.expect(SemanticError.class);
		
		//the scope containing foo was closed.
		this.symbolTable.retrieveSymbol("foo");
	}
	
	/**
	 * Test retrieving a symbol that does not exist in an open scope.
	 * @throws SemanticError
	 */
	@Test
	public void testRetrieveSymbolsInvalid2() throws SemanticError {
		this.symbolTable.openMajorScope();
				
		//A scope exists, but foo has not been declared in it.
		Symbol s = this.symbolTable.retrieveSymbol("foo");
		assertEquals(s, null);
		
		this.symbolTable.openMajorScope();
		this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
		this.symbolTable.closeCurrentScope();
				
		//the scope containing foo was closed; however, these still is some open scope.
		s = this.symbolTable.retrieveSymbol("foo");
		assertEquals(s, null);
	}
	
	/**
	 * Test addSymbol and retrieveSymbol when symbols exist in different scopes.
	 * @throws SemanticError
	 */
	@Test
	public void testMultipleScopes() throws SemanticError {
		this.symbolTable.openMajorScope();
		
		Symbol foo = this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
		
		this.symbolTable.openMajorScope();
		
		//foo exists in an upper scope.
		assertEquals(foo, this.symbolTable.retrieveSymbol("foo"));
		
		//Create a new symbol for foo in the current scope. This should be allowable
		//since the current scope is a major one.
		Symbol foo2 = this.symbolTable.addSymbolToCurScope("foo", new IntegerType());
		
		assertEquals(foo2, this.symbolTable.retrieveSymbol("foo"));
		
		//If the current scope is closed, the original foo should be retrieved.
		this.symbolTable.closeCurrentScope();
		
		assertEquals(foo, this.symbolTable.retrieveSymbol("foo"));
	}
	
	/**
	 * Test that an error is thrown if user attempts to redeclare a symbol in a minor scope.
	 * @throws SemanticError
	 */
	public void testRedeclareInMinorScope() throws SemanticError {
		this.symbolTable.openMajorScope();
		
		Symbol foo = this.symbolTable.addSymbolToCurScope("foo", new BooleanType());
		
		this.symbolTable.openMinorScope();
		
		//this.thrown.expect(SemanticError.class);
		Symbol bar = this.symbolTable.addSymbolToCurScope("foo", new IntegerType());
		
	}
}

package compiler488.junit;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import compiler488.semantics.SemanticError;
import compiler488.symbol.SymbolTable;

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
	public void testCloseScopeInvalid() throws SemanticError {
		//Close a scope before any scope has been opened.
		this.thrown.expect(SemanticError.class);
		this.symbolTable.closeCurrentScope();
		
		this.symbolTable.openMinorScope();
		this.symbolTable.closeCurrentScope();
		this.thrown.expect(SemanticError.class);
		this.symbolTable.closeCurrentScope();
	}
}

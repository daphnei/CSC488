//package compiler488.junit;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Set;
//import java.util.TreeSet;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//
//import compiler488.exceptions.SemanticErrorException;
//import compiler488.semantics.types.BooleanSemType;
//import compiler488.semantics.types.IntegerSemType;
//import compiler488.symbol.ScopeType;
//import compiler488.symbol.Symbol;
//import compiler488.symbol.SymbolTable;
//
//public class TestSymbolTable {
//	private SymbolTable symbolTable;
//	
//	@Rule
//    public ExpectedException thrown = ExpectedException.none();
//	
//	@Before
//    public void setUp() {
//		this.symbolTable = new SymbolTable();
//		this.symbolTable.Initialize();
//	}
//	
//	@After
//    public void tearDown() throws Exception {
//        this.symbolTable.Finalize();   
//    }
//	
//	/**
//	 * Test opening and closing scopes.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testOpenCloseScope() throws SemanticErrorException {
//		//Test before nay scope has been opened.
//		assertEquals(this.symbolTable.getCurrentScope(), -1);
//		
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		assertEquals(this.symbolTable.getCurrentScope(), 0);
//		
//		this.symbolTable.openScope(ScopeType.GENERIC);
//		assertEquals(this.symbolTable.getCurrentScope(), 1);
//		
//		this.symbolTable.closeCurrentScope();
//		assertEquals(this.symbolTable.getCurrentScope(), 0);
//	}
//
//	/**
//	 * Test closing more scopes than there are open.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testCloseScopeInvalid1() throws SemanticErrorException {
//		//Close a scope before any scope has been opened.
//		this.thrown.expect(SemanticErrorException.class);
//		this.symbolTable.closeCurrentScope();
//	}
//	
//	/**
//	 * Test closing more scopes than there are open.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testCloseScopeInvalid2() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		this.symbolTable.closeCurrentScope();
//		
//		this.thrown.expect(SemanticErrorException.class);
//		this.symbolTable.closeCurrentScope();
//	}
//	
//	/**
//	 * Test adding symbols within a single scope.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testAddSymbolsOneScope() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//	
//		Symbol s = this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//		Symbol s2 = this.symbolTable.retrieveSymbol("foo");
//		assertEquals(s, s2);
//		
//		s = this.symbolTable.addSymbolToCurScope("bar", new IntegerSemType());
//		s2 = this.symbolTable.retrieveSymbol("bar");
//		assertEquals(s, s2);
//	}
//	
//	/**
//	 * Test add symbol when no scope exists.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testAddSymbolsNoScope() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		this.symbolTable.closeCurrentScope();
//		this.thrown.expect(SemanticErrorException.class);
//		this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//	}
//	
//	/**
//	 * Test retrieving a symbol when there is no open scope.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testRetrieveSymbolsNoScope() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//		this.symbolTable.closeCurrentScope();
//		
//		this.thrown.expect(SemanticErrorException.class);
//		
//		//the scope containing foo was closed.
//		this.symbolTable.retrieveSymbol("foo");
//	}
//	
//	/**
//	 * Test retrieving a symbol that does not exist in an open scope.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testRetrieveSymbolsInvalid2() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//				
//		//A scope exists, but foo has not been declared in it.
//		Symbol s = this.symbolTable.retrieveSymbol("foo");
//		assertEquals(s, null);
//		
//		this.symbolTable.openScope(ScopeType.ROUTINE);
//		this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//		this.symbolTable.closeCurrentScope();
//				
//		//the scope containing foo was closed; however, these still is some open scope.
//		s = this.symbolTable.retrieveSymbol("foo");
//		assertEquals(s, null);
//	}
//	
//	/**
//	 * Test addSymbol and retrieveSymbol when symbols exist in different scopes.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testMultipleScopes() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		
//		Symbol foo = this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//		
//		this.symbolTable.openScope(ScopeType.ROUTINE);
//		
//		//foo exists in an upper scope.
//		assertEquals(foo, this.symbolTable.retrieveSymbol("foo"));
//		
//		//Create a new symbol for foo in the current scope. This should be allowable
//		//since the current scope is a major one.
//		Symbol foo2 = this.symbolTable.addSymbolToCurScope("foo", new IntegerSemType());
//		
//		assertEquals(foo2, this.symbolTable.retrieveSymbol("foo"));
//		
//		//If the current scope is closed, the original foo should be retrieved.
//		this.symbolTable.closeCurrentScope();
//		
//		assertEquals(foo, this.symbolTable.retrieveSymbol("foo"));
//	}
//	
//	/**
//	 * Test that an error is thrown if user attempts to redeclare a symbol in a minor scope.
//	 * @throws SemanticError
//	 */
//	@Test
//	public void testRedeclareInMinorScope() throws SemanticErrorException {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		
//		Symbol foo = this.symbolTable.addSymbolToCurScope("foo", new BooleanSemType());
//		
//		this.symbolTable.openScope(ScopeType.GENERIC);
//		
//		this.thrown.expect(SemanticErrorException.class);
//		Symbol bar = this.symbolTable.addSymbolToCurScope("foo", new IntegerSemType());
//		
//	}
//	
//	/**
//	 * Tests for the searchScopeType method
//	 */
//	@Test
//	public void testSearchScopesForType() {
//		this.symbolTable.openScope(ScopeType.PROGRAM);
//		this.symbolTable.openScope(ScopeType.ROUTINE);
//		this.symbolTable.openScope(ScopeType.LOOP);
//		this.symbolTable.openScope(ScopeType.ROUTINE);
//		this.symbolTable.openScope(ScopeType.GENERIC);
//		this.symbolTable.openScope(ScopeType.YIELD);
//		
//		Set<ScopeType> forbidden = new TreeSet<ScopeType>();
//		forbidden.add(ScopeType.ROUTINE);
//		
//		boolean foundGenericBeforeForbidden = this.symbolTable.searchScopesForType(ScopeType.GENERIC, forbidden);
//		assertEquals(foundGenericBeforeForbidden, true);
//		
//		boolean foundLoopBeforeForbidden = this.symbolTable.searchScopesForType(ScopeType.LOOP, forbidden);
//		assertEquals(foundLoopBeforeForbidden, false);
//
//		boolean foundProgramBeforeForbidden = this.symbolTable.searchScopesForType(ScopeType.PROGRAM, forbidden);
//		assertEquals(foundProgramBeforeForbidden, false);
//
//	}
//}

package compiler488.semantics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.decl.ArrayDeclPart;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.DeclarationPart;
import compiler488.ast.decl.MultiDeclarations;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.decl.ScalarDeclPart;
import compiler488.ast.expn.AnonFuncExpn;
import compiler488.ast.expn.ArithExpn;
import compiler488.ast.expn.BoolConstExpn;
import compiler488.ast.expn.BoolExpn;
import compiler488.ast.expn.CompareExpn;
import compiler488.ast.expn.EqualsExpn;
import compiler488.ast.expn.Expn;
import compiler488.ast.expn.FunctionCallExpn;
import compiler488.ast.expn.IdentExpn;
import compiler488.ast.expn.IntConstExpn;
import compiler488.ast.expn.NotExpn;
import compiler488.ast.expn.SkipConstExpn;
import compiler488.ast.expn.SubsExpn;
import compiler488.ast.expn.TextConstExpn;
import compiler488.ast.expn.UnaryMinusExpn;
import compiler488.ast.stmt.AssignStmt;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.GetStmt;
import compiler488.ast.stmt.IfStmt;
import compiler488.ast.stmt.LoopStmt;
import compiler488.ast.stmt.ProcedureCallStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.PutStmt;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Stmt;
import compiler488.ast.stmt.WhileDoStmt;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;
import compiler488.compiler.Main;
import compiler488.symbol.PrimitiveSemType;
import compiler488.symbol.SemType;
import compiler488.symbol.SymbolTable;
import compiler488.utilities.NodeVisitor;

/**
 * The Semantics class implements IVisitor. It will trace through an AST tree
 * and check each node for the appropriate semantic actions.
 * 
 * Please ensure each of the NodeVisitor methods call <i>super</i>! This will
 * ensure the AST is fully traversed.
 * 
 * @author g2robint
 */
public class Semantics extends NodeVisitor {

	/**
	 * Flag for tracing semantic analysis.
	 */
	private boolean traceSemantics = false;

	/**
	 * File sink for semantic analysis trace.
	 */
	private String traceFile = new String();
	public FileWriter Tracer;
	public File f;

	/**
	 * The symbol table we are using for this analysis.
	 */
	private SymbolTable symbolTable;
	private SemanticActions actions;	
	private PrimitiveSemType currentDeclarationType;

	public Semantics() {

	}

	/**
	 * Initialize the symbol table.
	 */
	public void Initialize() {
		this.symbolTable = new SymbolTable();
		this.symbolTable.Initialize();
		this.actions = new SemanticActions(this, this.symbolTable);
	}

	/**
	 * Finalize the symbol table.
	 */
	public void Finalize() {

	}
	
	public PrimitiveSemType getCurrentDeclarationType() {
		return this.currentDeclarationType;
	}
	
	public 

	/**
	 * Perform one semantic analysis action. It will record the action, perform
	 * it, catch any semantic errors, and report them to the compiler if they
	 * are found.
	 * 
	 * @param actionNumber
	 *            Semantic analysis action number.
	 */
	void semanticAction(int actionNumber, BaseAST visitable) {

		if (traceSemantics) {
			if (traceFile.length() > 0) {
				// Output trace to the file represented by traceFile.
				try {
					// Open the file for writing and append to it.
					File f = new File(traceFile);
					Tracer = new FileWriter(traceFile, true);
					Tracer.write("Sematics: S" + actionNumber + "\n");

					// Always be sure to close the file.
					Tracer.close();
				} catch (IOException e) {
					System.out.println(traceFile + " could be opened/created.  It may be in use.");
				}
			} else {
				// Output the trace to standard out.
				System.out.println("Sematics: S" + actionNumber);
			}
		}

		String errorMessage = null;
		try {
			// Perform the action in this helper class.
			this.actions.checkSemanticRule(actionNumber, visitable);
		} catch (InvalidScopeException exception) {
			errorMessage = "Trying to operate on a non-existent scope.";
		} catch (SymbolConflictException exception) {
			errorMessage = "Identifier " + exception.symbolName + " has already been declared.";
		} catch (UndeclaredSymbolException exception) {
			errorMessage = "Identifier " + exception.symbolName + " is not declared in this scope.";
		} catch (InvalidArrayBoundsException exception) {
			errorMessage = "The declared array " + exception.name + " has invalid array bounds.";
		} catch (SemanticErrorException error) {
			errorMessage = error.getMessage();
			if (errorMessage.equals("")) {
				errorMessage = "Unknown Error.";
			}
		}
		if (errorMessage != null) {
			// TODO: GET ACTUAL LINE NUMBER.
			System.out.println("SEMANTIC ERROR (Line " + visitable.getRightColumnNumber() + "): " + errorMessage);
			Main.errorOccurred = true;
		}

		System.out.println("Semantic Action: S" + actionNumber);
		return;
	}

	// --- Program ---

	@Override
	public void visit(Program visitable) {
		this.semanticAction(00, visitable);
		super.visit(visitable);
		this.semanticAction(01, visitable);
	}

	// --- Statement ---

	@Override
	public void visit(Stmt visitable) {
		// This is an abstract class, do nothing.
		super.visit(visitable);
		this.printAbstractWarning();
	}

	@Override
	public void visit(AssignStmt visitable) {
		super.visit(visitable);
		this.semanticAction(34, visitable);
	}

	@Override
	public void visit(IfStmt visitable) {
		super.visit(visitable);
		this.semanticAction(30, visitable.getCondition());
	}

	@Override
	public void visit(WhileDoStmt visitable) {
		super.visit(visitable);
		this.semanticAction(30, visitable.getExpn());
	}

	@Override
	public void visit(LoopStmt visitable) {
		super.visit(visitable);
		// Do nothing.
	}

	@Override
	public void visit(ExitStmt visitable) {
		super.visit(visitable);
		// TODO: Needs scope refactor
	}

	@Override
	public void visit(PutStmt visitable) {
		super.visit(visitable);

		for (Printable p : visitable.getOutputs()) {
			Expn expression = (Expn) p;

			// HACK: SkipConst and TextConst are considered integers so that
			// this passes.
			this.semanticAction(31, expression);
		}
	}

	@Override
	public void visit(GetStmt visitable) {
		super.visit(visitable);
		for (Expn e : visitable.getInputs()) {
			this.semanticAction(31, e);
		}
	}

	@Override
	public void visit(ProcedureCallStmt visitable) {
		super.visit(visitable);
		// TODO: Needs scope refactor
	}

	@Override
	public void visit(ReturnStmt visitable) {
		super.visit(visitable);
		// TODO: Needs scope refactor
	}

	@Override
	public void visit(Scope visitable) {
		this.semanticAction(06, visitable);
		super.visit(visitable);
		this.semanticAction(07, visitable);
	}

	// --- Declarations ---

	@Override
	public void visit(Declaration visitable) {
		// This is an abstract class, do nothing.
		super.visit(visitable);
		this.printAbstractWarning();
	}

	@Override
	public void visit(MultiDeclarations visitable) {
		// HACK: Keep track of the declaration state and use it later.
		this.currentDeclarationType = visitable.getType().getSemanticType();
		super.visit(visitable);
		this.semanticAction(47, visitable);
		this.currentDeclarationType = null;		
	}

	@Override
	public void visit(DeclarationPart visitable) {
		super.visit(visitable);
		this.printAbstractWarning();
	}
	
	@Override
	public void visit(ArrayDeclPart visitable) {
		super.visit(visitable);
		this.semanticAction(46, visitable);
		this.semanticAction(19, visitable);
		this.semanticAction(48, visitable); // NOTE: This does nothing.
	}
	
	@Override
	public void visit(ScalarDeclPart visitable) {
		super.visit(visitable);
		this.semanticAction(10, visitable);
	}

	@Override
	public void visit(RoutineDecl visitable) {
		super.visit(visitable);
		// TODO: Need to define function type.
	}

	@Override
	public void visit(ScalarDecl visitable) {
		super.visit(visitable);
		this.semanticAction(15, visitable);
	}



	// --- Expressions ---

	@Override
	public void visit(Expn visitable) {
		super.visit(visitable);
		this.printAbstractWarning();
	}

	@Override
	public void visit(UnaryMinusExpn visitable) {
		super.visit(visitable);
		this.semanticAction(21, visitable.getOperand());
		this.semanticAction(31, visitable);
	}

	@Override
	public void visit(ArithExpn visitable) {
		super.visit(visitable);
		this.semanticAction(31, visitable.getFirstExpression());
		this.semanticAction(31, visitable.getSecondExpression());
		this.semanticAction(21, visitable);
	}

	@Override
	public void visit(BoolConstExpn visitable) {
		super.visit(visitable);
		this.semanticAction(20, visitable);
	}

	@Override
	public void visit(NotExpn visitable) {
		super.visit(visitable);
		this.semanticAction(30, visitable);
		this.semanticAction(20, visitable);
	}

	@Override
	public void visit(BoolExpn visitable) {
		super.visit(visitable);
		this.semanticAction(30, visitable.getFirstExpression());
		this.semanticAction(30, visitable.getSecondExpression());
		this.semanticAction(20, visitable);
	}

	@Override
	public void visit(EqualsExpn visitable) {
		super.visit(visitable);
		this.semanticAction(32, visitable);
		this.semanticAction(20, visitable);
	}

	@Override
	public void visit(CompareExpn visitable) {
		super.visit(visitable);
		this.semanticAction(31, visitable.getFirstExpression());
		this.semanticAction(31, visitable.getSecondExpression());
		this.semanticAction(20, visitable);
	}

	@Override
	public void visit(AnonFuncExpn visitable) {
		super.visit(visitable);
		this.semanticAction(24, visitable);
	}

	@Override
	public void visit(FunctionCallExpn visitable) {
		super.visit(visitable);
		this.semanticAction(28, visitable);
	}

	@Override
	public void visit(IdentExpn visitable) {
		super.visit(visitable);
		this.semanticAction(37, visitable);
		this.semanticAction(39, visitable);
		this.semanticAction(26, visitable);
		this.semanticAction(25, visitable);
	}

	@Override
	public void visit(IntConstExpn visitable) {
		super.visit(visitable);
		this.semanticAction(21, visitable);
	}

	@Override
	public void visit(SubsExpn visitable) {
		super.visit(visitable);
		this.semanticAction(38, visitable);
		this.semanticAction(27, visitable);
	}

	@Override
	public void visit(SkipConstExpn visitable) {
		super.visit(visitable);
		// Do nothing.
	}

	@Override
	public void visit(TextConstExpn visitable) {
		super.visit(visitable);
		// Do nothing.
	}

	// --- Types ---

	@Override
	public void visit(BooleanType visitable) {
		this.semanticAction(22, visitable);
		super.visit(visitable);
	}

	@Override
	public void visit(IntegerType visitable) {
		this.semanticAction(21, visitable);
		super.visit(visitable);
	}

	@Override
	public void visit(Type visitable) {
		super.visit(visitable);
		this.printAbstractWarning();
	}

	// Private Helper Methods

	private void printAbstractWarning() {
		System.out.println("WARNING: This code should not be run! This is an abstract class!!!");
	}
}

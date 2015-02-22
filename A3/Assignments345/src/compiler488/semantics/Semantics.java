package compiler488.semantics;

import java.io.*;

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
import compiler488.symbol.SymbolTable;
import compiler488.utilities.IVisitor;

/**
 * The Semantics class implements IVisitor. It will trace through an AST tree
 * and check each node for the appropriate semantic actions.
 * 
 * @author g2robint
 */
public class Semantics implements IVisitor {

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

	
	public Semantics() {

	}

	/**
	 * Initialize the symbol table.
	 */
	public void Initialize() {
		this.symbolTable = new SymbolTable();
		this.symbolTable.Initialize();
	}

	/**
	 * Finalize the symbol table.
	 */
	public void Finalize() {

	}

	/**
	 * Perform one semantic analysis action.
	 * 
	 * @param actionNumber
	 *            Semantic analysis action number.
	 */
	void semanticAction(int actionNumber) {

		if (traceSemantics) {
			if (traceFile.length() > 0) {
				// output trace to the file represented by traceFile
				try {
					// open the file for writing and append to it
					File f = new File(traceFile);
					Tracer = new FileWriter(traceFile, true);

					Tracer.write("Sematics: S" + actionNumber + "\n");
					// always be sure to close the file
					Tracer.close();
				} catch (IOException e) {
					System.out.println(traceFile + " could be opened/created.  It may be in use.");
				}
			} else {
				// output the trace to standard out.
				System.out.println("Sematics: S" + actionNumber);
			}

		}

		/*************************************************************/
		/* Code to implement each semantic action GOES HERE          */
		/* This stub semantic analyzer just prints the actionNumber  */
		/*                                                           */
		/* FEEL FREE TO ignore or replace this procedure             */
		/*************************************************************/

		System.out.println("Semantic Action: S" + actionNumber);
		return;
	}

	@Override
	public void visit(Program visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArrayDeclPart visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Declaration visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DeclarationPart visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MultiDeclarations visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(RoutineDecl visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ScalarDecl visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ScalarDeclPart visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AnonFuncExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArithExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BoolConstExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BoolExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CompareExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(EqualsExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Expn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FunctionCallExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IdentExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntConstExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(NotExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SkipConstExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SubsExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TextConstExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UnaryMinusExpn visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AssignStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExitStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(GetStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IfStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LoopStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ProcedureCallStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PutStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ReturnStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Scope visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Stmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhileDoStmt visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BooleanType visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IntegerType visitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Type visitable) {
		// TODO Auto-generated method stub

	}

	// ADDITIONAL FUNCTIONS TO IMPLEMENT SEMANTIC ANALYSIS GO HERE

}

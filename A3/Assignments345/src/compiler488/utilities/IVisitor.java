package compiler488.utilities;

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


public interface IVisitor {	
	public void visit(ArrayDeclPart visitable);
	public void visit(Declaration visitable);
	public void visit(DeclarationPart visitable);
	public void visit(MultiDeclarations visitable);
	public void visit(RoutineDecl visitable);
	public void visit(ScalarDecl visitable);
	public void visit(ScalarDeclPart visitable);
	
	public void visit(AnonFuncExpn visitable);
	public void visit(ArithExpn visitable);
	public void visit(BoolConstExpn visitable);
	public void visit(BoolExpn visitable);
	public void visit(CompareExpn visitable);
	public void visit(EqualsExpn visitable);
	public void visit(Expn visitable);
	public void visit(FunctionCallExpn visitable);
	public void visit(IdentExpn visitable);
	public void visit(IntConstExpn visitable);
	public void visit(NotExpn visitable);
	public void visit(SkipConstExpn visitable);
	public void visit(SubsExpn visitable);
	public void visit(TextConstExpn visitable);
	public void visit(UnaryMinusExpn visitable);

	public void visit(AssignStmt visitable);
	public void visit(ExitStmt visitable);
	public void visit(GetStmt visitable);
	public void visit(IfStmt visitable);
	public void visit(LoopStmt visitable);
	public void visit(ProcedureCallStmt visitable);
	public void visit(Program visitable);
	public void visit(PutStmt visitable);
	public void visit(ReturnStmt visitable);
	public void visit(Scope visitable);
	public void visit(Stmt visitable);
	public void visit(WhileDoStmt visitable);

	public void visit(BooleanType visitable);
	public void visit(IntegerType visitable);
	public void visit(Type visitable);
}

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

public class NodeVisitor implements IVisitor {

	@Override
	public void visit(ArrayDeclPart visitable) {
		// No children.		
	}

	@Override
	public void visit(Declaration visitable) {
		// No children.
	}

	@Override
	public void visit(DeclarationPart visitable) {
		// No children.		
	}

	@Override
	public void visit(MultiDeclarations visitable) {
		for (DeclarationPart element : visitable.getParts()) {
			element.accept(this);
		}		
	}

	@Override
	public void visit(RoutineDecl visitable) {
		for (ScalarDecl param : visitable.getParameters()) {
			param.accept(this);
		}
		visitable.getBody().accept(this);		
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
	public void visit(Program visitable) {
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

}

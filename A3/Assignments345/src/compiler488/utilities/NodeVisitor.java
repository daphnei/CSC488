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

/**
 * The NodeVisitor class traverses an AST and calls accept on each node.
 * This class should be extended so that work is done at each node.
 * 
 * @author g2robint
 */
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
		// No children.	
	}

	@Override
	public void visit(ScalarDeclPart visitable) {
		// No children.
	}

	@Override
	public void visit(AnonFuncExpn visitable) {
		// TODO: Am I accepting these in the right order?		
		for (Stmt bodyStmt : visitable.getBody()) {
			bodyStmt.accept(this);
		}
		visitable.getExpn().accept(this);
	}

	@Override
	public void visit(ArithExpn visitable) {
		visitable.getFirst().accept(this);
		visitable.getSecond().accept(this);		
	}

	@Override
	public void visit(BoolConstExpn visitable) {
		// No children.
		
	}

	@Override
	public void visit(BoolExpn visitable) {
		visitable.getFirst().accept(this);
		visitable.getSecond().accept(this);
	}

	@Override
	public void visit(CompareExpn visitable) {
		visitable.getFirst().accept(this);
		visitable.getSecond().accept(this);		
	}

	@Override
	public void visit(EqualsExpn visitable) {
		visitable.getFirst().accept(this);
		visitable.getSecond().accept(this);		
	}

	@Override
	public void visit(Expn visitable) {
		// Abstract class - no children.
	}

	@Override
	public void visit(FunctionCallExpn visitable) {		
		if (visitable.getArguments() != null) {
			for (Expn arg : visitable.getArguments()) {
				arg.accept(this);
			}
		}		
	}

	@Override
	public void visit(IdentExpn visitable) {
		// No children.		
	}

	@Override
	public void visit(IntConstExpn visitable) {
		// No children.
		
	}

	@Override
	public void visit(NotExpn visitable) {
		visitable.getOperand().accept(this);
		
	}

	@Override
	public void visit(SkipConstExpn visitable) {
		// No children.		
	}

	@Override
	public void visit(SubsExpn visitable) {
		visitable.getSubscript1().accept(this);
		if (visitable.getSubscript2() != null) {
			visitable.getSubscript2().accept(this);
		}
	}

	@Override
	public void visit(TextConstExpn visitable) {
		// No children.
	}

	@Override
	public void visit(UnaryMinusExpn visitable) {
		visitable.getOperand().accept(this);		
	}

	@Override
	public void visit(AssignStmt visitable) {
		visitable.getLval().accept(this);
		visitable.getRval().accept(this);		
	}

	@Override
	public void visit(ExitStmt visitable) {
		visitable.getExpn().accept(this);		
	}

	@Override
	public void visit(GetStmt visitable) {
    	for (Expn input : visitable.getInputs()) {
    		input.accept(this);
    	}
	}

	@Override
	public void visit(IfStmt visitable) {
		visitable.getCondition().accept(this);    	
    	
    	for (Stmt trueStmt : visitable.getWhenTrue()) {
    		trueStmt.accept(this);
    	}
    	
    	// If there is no else, then "whenFalse" will be null.
    	if (visitable.getWhenFalse() != null) {
	    	for (Stmt falseStmt : visitable.getWhenFalse()) {
	    		falseStmt.accept(this);
	    	}
    	}
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

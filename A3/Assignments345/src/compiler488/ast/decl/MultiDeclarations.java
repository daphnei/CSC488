package compiler488.ast.decl;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;
import compiler488.interfaces.IVisitor;

/**
 * Holds the declaration of multiple elements.
 */
public class MultiDeclarations extends Declaration {
    /** The parts being declared */
    private ASTList<DeclarationPart> elements;

    public MultiDeclarations(Type type, ASTList<DeclarationPart> elements,int left, int right) {
        super(null, type, left, right);

        this.elements = elements;
    }

    public ASTList<DeclarationPart> getParts() {
        return elements;
    }

    @Override
	public void prettyPrint(PrettyPrinter p) {
        p.print(type + " ");
        elements.prettyPrintCommas(p);
    }
    
	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);	
	}

}

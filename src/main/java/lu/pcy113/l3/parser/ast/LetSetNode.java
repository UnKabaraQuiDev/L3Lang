package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class LetSetNode extends ExprNode implements RecursiveArithmeticOp {

	public LetSetNode(Node let, Node expr) {
		add(let);
		add(expr);
	}
	
	@Override
	public boolean isDecimal() throws CompilerException {
		TypeNode type = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		return type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) type).isDecimal();
	}
	
	@Override
	public boolean isInteger() throws CompilerException {
		TypeNode type = getClosestContainer().getLetDefDescriptor(this).getNode().getType();
		return type instanceof PrimitiveTypeNode && ((PrimitiveTypeNode) type).isInteger();
	}

	public FieldAccessNode getLet() {
		return (FieldAccessNode) children.get(0);
	}

	public ExprNode getExpr() {
		return (ExprNode) children.get(1);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getLet() + ")";
	}

	/*public IdentifierToken getLetIdent() {
		Node let = getLet();
		if (let instanceof PointerDerefNode) {
			PointerDerefNode llet = (PointerDerefNode) let;
			if (llet.getNode() instanceof FieldAccessNode) {
				return (IdentifierToken) ((FieldAccessNode) (llet).getNode()).getMainIdent();
			} else if (llet.getNode() instanceof FunCallNode) {
				return (IdentifierToken) ((FunCallNode) (llet).getNode()).getIdent();
			}
		} else if (let instanceof FieldAccessNode) {
			return ((FieldAccessNode) let).getMainIdent();
		}
		return null;
	}*/

}

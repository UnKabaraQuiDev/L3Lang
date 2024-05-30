package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public class LetSetNode extends ExprNode implements RecursiveArithmeticOp {

	public LetSetNode(Node let, Node expr) {
		add(let);
		add(expr);
	}
	
	@Override
	public boolean isDecimal() throws L3Exception {
		return getClosestContainer().getLetDefDescriptor(this).getNode().getType().isDecimal();
	}
	
	@Override
	public boolean isInteger() throws L3Exception {
		return getClosestContainer().getLetDefDescriptor(this).getNode().getType().isInteger();
	}

	public Node getLet() {
		return (Node) children.get(0);
	}

	public Node getExpr() {
		return children.get(1);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getLet() + ")";
	}

	public IdentifierToken getLetIdent() {
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
	}

}

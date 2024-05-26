package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetTypeSetNode extends Node implements RecursiveArithmeticOp {

	public LetTypeSetNode(Node let, Node expr) {
		add(let);
		add(expr);
	}
	
	@Override
	public boolean isFloat() throws L3Exception {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isFloat();
	}
	
	@Override
	public boolean isInt() throws L3Exception {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isInt();
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
		if (let instanceof DelocalizingNode) {
			DelocalizingNode llet = (DelocalizingNode) let;
			if (llet.getNode() instanceof VarNumNode) {
				return (IdentifierToken) ((VarNumNode) (llet).getNode()).getMainIdent();
			} else if (llet.getNode() instanceof FunCallNode) {
				return (IdentifierToken) ((FunCallNode) (llet).getNode()).getIdent();
			}
		} else if (let instanceof VarNumNode) {
			return ((VarNumNode) let).getMainIdent();
		}
		return null;
	}

}

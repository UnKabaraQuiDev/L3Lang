package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class LetTypeSetNode extends Node {

	public LetTypeSetNode(Node let, Node expr) {
		add(let);
		add(expr);
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

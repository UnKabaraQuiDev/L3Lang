package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class LetRefNode extends ExprNode {
	
	public LetRefNode(Node node) {
		add(node);
	}

	public Node getNode() {
		return children.get(0);
	}
	
}

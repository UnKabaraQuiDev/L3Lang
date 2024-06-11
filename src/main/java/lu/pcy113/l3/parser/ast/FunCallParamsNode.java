package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class FunCallParamsNode extends Node {

	public ExprNode getParam(int i) {
		return (ExprNode) children.get(i);
	}

}

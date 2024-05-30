package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class PrimitiveTypeAllocNode extends Node {

	public PrimitiveTypeAllocNode(TypeNode type, ExprNode node) {
		add(type);
		add(node);
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public ExprNode getNode() {
		return (ExprNode) children.get(1);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}

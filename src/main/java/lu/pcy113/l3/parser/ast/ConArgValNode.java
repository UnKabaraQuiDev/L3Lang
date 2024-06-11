package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.lit.StringLitNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class ConArgValNode extends Node {

	private int index, stackSize = 0;

	public ConArgValNode(int index, Node node) {
		this.index = index;
		add(node);
	}

	public TypeNode getType() {
		Node expr = getExpr();
		if (expr instanceof NumLitNode) {
			return new TypeNode(TokenType.NUM_LIT);
		} else if (expr instanceof FieldAccessNode) {
			return ((LetScopeDescriptor) expr.getClosestContainer().getClosestDescriptor(((FieldAccessNode) expr).getMainIdent().getValue())).getNode().getType();
		} else if (expr instanceof FunCallNode) {
			return ((FunScopeDescriptor) expr.getClosestContainer().getClosestDescriptor(((FunCallNode) expr).getIdent().getValue())).getNode().getReturnType();
		} else if (expr instanceof StringLitNode) {
			return ((StringLitNode) expr).getType();
		}
		return null;
	}

	public Node getExpr() {
		return children.get(0);
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getType() + ", " + index + ", size=" + getStackSize() + ")";
	}

}

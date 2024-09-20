package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class ArrayTypeNode extends TypeNode {

	private int elementCount;

	public ArrayTypeNode(TypeNode node, int value) {
		add(node);
		elementCount = value;
	}

	public ArrayTypeNode getSubType(int arrayDepth) {
		ArrayTypeNode node = this;
		for (int i = 0; i < arrayDepth; i++) {
			node = (ArrayTypeNode) node.getSubType();
		}
		return node;
	}
	
	public TypeNode getSubType() {
		return (TypeNode) getChildren().get(0);
	}

	public int getElementCount() {
		return elementCount;
	}

	@Override
	public void normalizeSize(ScopeContainer container) {
		sizeOverride = true;
		getSubType().normalizeSize(container);
		bytesOverride = getSubType().getBytesSize() * elementCount;
	}

	@Override
	public int getBytesSize() {
		return bytesOverride;
	}

	@Override
	public boolean typeMatches(ExprNode param) throws CompilerException {
		throw new CompilerException("Not implemented.");
	}

	@Override
	public String toString() {
		return super.toString() + "(elementCount=" + elementCount + ", size=" + bytesOverride + ")";
	}

}

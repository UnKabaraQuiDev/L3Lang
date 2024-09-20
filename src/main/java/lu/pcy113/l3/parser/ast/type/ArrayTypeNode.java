package lu.pcy113.l3.parser.ast.type;

import java.lang.management.MemoryType;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class ArrayTypeNode extends TypeNode {

	private int elementCount;

	public ArrayTypeNode(TypeNode node, int value) {
		add(node);
		elementCount = value;
	}

	public TypeNode getSubType(int arrayDepth) {
		TypeNode node = this;
		for (int i = 0; i < arrayDepth; i++) {
			if (node instanceof ArrayTypeNode) {
				node = ((ArrayTypeNode) node).getSubType();
			} else {
				if (i == arrayDepth - 1) {
					throw new RuntimeException(new CompilerException("Depth exceeding type depth."));
				}
				return node;
			}
		}
		return node;
	}

	public TypeNode getSubType() {
		return (TypeNode) getChildren().get(0);
	}

	public TypeNode getLastSubType() {
		ArrayTypeNode arr = this;
		while (arr.getSubType() instanceof ArrayTypeNode) {
			arr = (ArrayTypeNode) arr.getSubType();
		}
		return arr.getSubType();
	}

	public int getElementCount() {
		return elementCount;
	}

	@Override
	public void normalizeSize(ScopeContainer container) {
		sizeOverride = true;
		bytesOverride = MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE);
		getSubType().normalizeSize(container);
		// bytesOverride = getSubType().getBytesSize();
	}

	public int getRealByteSize() {
		if (sizeOverride) {
			return (getSubType() instanceof ArrayTypeNode ? ((ArrayTypeNode) getSubType()).getRealByteSize() : getSubType().getBytesSize()) * elementCount + MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE);
		} else {
			throw new RuntimeException(new CompilerException("Normalize size first."));
		}
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

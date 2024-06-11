package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class PointerTypeNode extends TypeNode {

	public PointerTypeNode(TypeNode node) {
		add(node);
	}

	public TypeNode getNode() {
		return (TypeNode) children.get(0);
	}

	@Override
	public boolean typeMatches(ExprNode param) {
		// TODO
		return false;
	}

	@Override
	public void normalizeSize() throws CompilerException {
		// Do nothing
	}

	@Override
	public int getBytesSize() throws CompilerException {
		return sizeOverride ? bytesOverride : MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE);
	}

	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

}

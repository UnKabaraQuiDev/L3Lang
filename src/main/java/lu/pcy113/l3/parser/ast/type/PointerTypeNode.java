package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class PointerTypeNode extends PrimitiveTypeNode {

	public PointerTypeNode(TypeNode node) {
		super(MemoryUtil.POINTER_TYPE);
		add(node);
	}

	public TypeNode getTypeNode() {
		return (TypeNode) children.get(0);
	}

	@Override
	public boolean typeMatches(ExprNode param) {
		// TODO
		return false;
	}

	@Override
	public void normalizeSize(ScopeContainer container) {
		// Do nothing
	}

	@Override
	public int getBytesSize() {
		try {
			return sizeOverride ? bytesOverride : MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE);
		} catch (CompilerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBytesSize(int bytes) {
		sizeOverride = true;
		bytesOverride = bytes;
	}

}

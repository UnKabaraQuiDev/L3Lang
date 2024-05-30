package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.MemoryUtil;

public class PointerTypeNode extends TypeNode {

	public PointerTypeNode(TypeNode node) {
		add(node);
	}

	public TypeNode getNode() {
		return (TypeNode) children.get(0);
	}
	
	@Override
	public int getBytesSize() throws CompilerException {
		return MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE);
	}

}

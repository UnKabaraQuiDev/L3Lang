package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class LetScopeDescriptor extends ScopeDescriptor {

	private LetDefNode node;

	private int size;
	private int offset;

	public LetScopeDescriptor(IdentifierLitNode ident, LetDefNode node) {
		super(ident);
		this.node = node;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public int getOffset() {
		return offset;
	}

	public LetDefNode getNode() {
		return node;
	}

}

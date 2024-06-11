package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class LetScopeDescriptor extends ScopeDescriptor {

	private LetDefNode node;

	private int offset;
	private boolean allocated = false;

	public LetScopeDescriptor(IdentifierLitNode ident, LetDefNode node) {
		super(ident);
		this.node = node;
	}

	public LetDefNode getNode() {
		return node;
	}

	public void setStackOffset(int offset) {
		this.offset = offset;
	}

	public int getStackOffset() {
		return offset;
	}

	public boolean isAllocated() {
		return allocated;
	}

	public void setAllocated(boolean allocated) {
		this.allocated = allocated;
	}

}

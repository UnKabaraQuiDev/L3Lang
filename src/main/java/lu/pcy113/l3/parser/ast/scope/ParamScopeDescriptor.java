package lu.pcy113.l3.parser.ast.scope;

import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class ParamScopeDescriptor extends ScopeDescriptor {

	private FunDefParamNode node;

	private int offset;

	public ParamScopeDescriptor(IdentifierLitNode ident, FunDefParamNode node) {
		super(ident);
		this.node = node;
	}

	public FunDefParamNode getNode() {
		return node;
	}

	public void setStackOffset(int offset) {
		this.offset = offset;
	}

	public int getStackOffset() {
		return offset;
	}

}

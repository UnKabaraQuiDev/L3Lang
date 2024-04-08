package lu.pcy113.l3.compiler;

import lu.pcy113.l3.parser.ast.LetTypeDefNode;

public class ScopeVarDefinition {

	private LetTypeDefNode node;
	private boolean iStatic = false;
	private String asmName, codeName;
	private int byteCount;

	public ScopeVarDefinition(LetTypeDefNode _node, String _codeName, String _asmName, boolean _iStatic, int byteCount) {
		this.node = _node;
		this.asmName = _asmName;
		this.iStatic = _iStatic;
		this.codeName = _codeName;
		this.byteCount = byteCount;
	}

	public String getCodeName() {
		return codeName;
	}

	public String getAsmName() {
		return asmName;
	}

	public boolean isiStatic() {
		return iStatic;
	}

	public LetTypeDefNode getNode() {
		return node;
	}

	public int getByteCount() {
		return byteCount;
	}

}

package lu.pcy113.l3.compiler;

import lu.pcy113.l3.parser.ast.LetTypeDefNode;

public class ScopeVarDefinition {

	private LetTypeDefNode node;
	private boolean iStatic = false;
	private String asmName, codeName;

	public ScopeVarDefinition(LetTypeDefNode _node, String _codeName, String _asmName, boolean _iStatic) {
		this.node = _node;
		this.asmName = _asmName;
		this.iStatic = _iStatic;
		this.codeName = _codeName;
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

}

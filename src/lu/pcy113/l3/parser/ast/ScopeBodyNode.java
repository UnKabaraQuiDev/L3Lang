package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class ScopeBodyNode extends ScopeContainerNode {

	private String clnAsmName;

	public String getClnAsmName() {
		return clnAsmName;
	}

	public void setClnAsmName(String clnAsmName) {
		this.clnAsmName = clnAsmName;
	}

}

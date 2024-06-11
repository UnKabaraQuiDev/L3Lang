package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class ScopeBodyNode extends ScopeContainerNode implements AsmNamed {

	private String asmName;

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

}

package lu.pcy113.l3.parser.ast;

public class FinallyDefNode extends Node implements AsmNamed {

	private String asmName;

	public FinallyDefNode(ScopeBodyNode body) {
		add(body);
	}

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(0);
	}

}

package lu.pcy113.l3.parser.ast;

public class ElseDefNode extends Node {

	private String asmName;

	public String getAsmName() {
		return asmName;
	}

	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(0);
	}

}

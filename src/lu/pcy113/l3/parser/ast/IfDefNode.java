package lu.pcy113.l3.parser.ast;

public class IfDefNode extends Node {

	private String asmName;
	
	public IfDefNode(Node condition) {
		add(condition);
	}
	
	public String getAsmName() {
		return asmName;
	}
	
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}
	
	public Node getCondition() {
		return children.get(0);
	}
	
	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(1);
	}

}

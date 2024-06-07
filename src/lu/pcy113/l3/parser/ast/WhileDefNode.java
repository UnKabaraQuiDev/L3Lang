package lu.pcy113.l3.parser.ast;

public class WhileDefNode extends Node implements AsmNamed {

	private String asmName;

	private boolean condition = false;;

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}

	public boolean hasCondition() {
		return condition;
	}

	public Node getCondition() {
		int index = (hasCondition() ? 1 : 0);
		return children.get(index - 1);
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.getLast();
	}

}

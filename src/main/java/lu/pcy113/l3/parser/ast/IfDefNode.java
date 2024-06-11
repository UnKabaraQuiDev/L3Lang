package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class IfDefNode extends Node implements AsmNamed {

	private String asmName;

	public IfDefNode(ExprNode conditionExpr, ScopeBodyNode body) {
		add(conditionExpr);
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

	public Node getCondition() {
		return children.get(0);
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(1);
	}

}

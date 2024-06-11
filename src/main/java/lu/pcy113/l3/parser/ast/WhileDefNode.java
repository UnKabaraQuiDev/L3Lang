package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class WhileDefNode extends Node implements AsmNamed {

	private String asmName;

	private boolean condition = false;

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

	public ExprNode getCondition() {
		return (ExprNode) children.getFirst();
	}

	public boolean hasFinally() {
		return children.stream().anyMatch(n -> n instanceof FinallyDefNode);
	}

	public boolean hasElse() {
		return children.stream().anyMatch(n -> n instanceof ElseDefNode);
	}

	public FinallyDefNode getFinally() throws CompilerException {
		return (FinallyDefNode) children.stream().filter(n -> n instanceof FinallyDefNode).findFirst().orElseThrow(() -> new CompilerException("While node has no finally statement."));
	}

	public ElseDefNode getElse() throws CompilerException {
		return (ElseDefNode) children.stream().filter(n -> n instanceof ElseDefNode).findFirst().orElseThrow(() -> new CompilerException("While node has no else statement."));
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(hasCondition() ? 1 : 0);
	}

}

package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class ForDefNode extends ScopeContainerNode implements AsmNamed {

	private String asmName;

	private boolean let = false, condition = false, inc = false;

	public ForDefNode() {
	}

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	public void setLet(boolean let) {
		this.let = let;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}

	public void setInc(boolean inc) {
		this.inc = inc;
	}

	public boolean hasLet() {
		return let;
	}

	public boolean hasCondition() {
		return condition;
	}

	public boolean hasInc() {
		return inc;
	}

	public Node getLet() {
		int index = (hasLet() ? 1 : 0);
		return children.get(index - 1);
	}

	public Node getCondition() {
		int index = (hasLet() ? 1 : 0) + (hasCondition() ? 1 : 0);
		return children.get(index - 1);
	}

	public Node getInc() {
		int index = (hasLet() ? 1 : 0) + (hasCondition() ? 1 : 0) + (hasInc() ? 1 : 0);
		return children.get(index - 1);
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
		return (ScopeBodyNode) children.get((hasCondition() ? 1 : 0) + (hasInc() ? 1 : 0) + (hasLet() ? 1 : 0));
	}

}

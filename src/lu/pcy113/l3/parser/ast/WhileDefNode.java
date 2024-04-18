package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.tokens.Token;

public class WhileDefNode extends Node {

	private Token token;
	private String asmName;

	public WhileDefNode(Token token, Node condition) {
		add(condition);
		this.token = token;
	}

	public boolean hasElse() {
		return children.stream().anyMatch(c -> c instanceof ElseDefNode);
	}

	public boolean hasFinally() {
		return children.stream().anyMatch(c -> c instanceof FinallyDefNode);
	}

	public ElseDefNode getElse() throws CompilerException {
		return (ElseDefNode) children.stream().filter(c -> c instanceof ElseDefNode).findFirst().orElseThrow(() -> new CompilerException("While statement has no else statement."));
	}

	public FinallyDefNode getFinally() throws CompilerException {
		return (FinallyDefNode) children.stream().filter(c -> c instanceof FinallyDefNode).findFirst().orElseThrow(() -> new CompilerException("While statement has no finally statement."));
	}

	public Token getToken() {
		return token;
	}

	public String getAsmName() {
		return asmName;
	}

	public void setAsmName(String asmName) {
		this.asmName = asmName;
		getBody().setClnAsmName(asmName + "_cln");
	}

	public Node getCondition() {
		return children.get(0);
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(1);
	}

}

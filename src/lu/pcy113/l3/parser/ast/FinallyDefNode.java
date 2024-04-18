package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class FinallyDefNode extends Node {

	private Token token;
	private String asmName;

	public FinallyDefNode(Token token) {
		this.token = token;
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

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(0);
	}

}

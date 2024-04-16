package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;

public class WhileDefNode extends Node {

	private Token token;
	private String asmName;
	
	public WhileDefNode(Token token, Node condition) {
		add(condition);
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
		getBody().setClnAsmName(asmName+"_cln");
	}
	
	public Node getCondition() {
		return children.get(0);
	}
	
	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.get(1);
	}

}

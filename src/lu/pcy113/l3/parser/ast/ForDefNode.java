package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;

public class ForDefNode extends ScopeContainerNode {

	private Token token;
	private String asmName;

	private boolean letTypeDef, condition, shortBody;

	public ForDefNode(Token token) {
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

	/*
	 * public void setLetTypeDef(Node node) { if (hasLetTypeDef()) return;
	 * add(node); letTypeDef = true; }
	 * 
	 * public void setCondition(Node node) { if (hasCondition()) return; add(node);
	 * condition = true; }
	 * 
	 * public void setShortBody(Node node) { if (hasShortBody()) return; add(node);
	 * shortBody = true; }
	 */

	public void setLetTypeDef(boolean letTypeDef) {
		this.letTypeDef = letTypeDef;
	}

	public void setShortBody(boolean shortBody) {
		this.shortBody = shortBody;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}

	public boolean hasLetTypeDef() {
		return letTypeDef;
	}

	public boolean hasCondition() {
		return condition;
	}

	public boolean hasShortBody() {
		return shortBody;
	}

	public Node getLetTypeDef() {
		int index = (hasLetTypeDef() ? 1 : 0);
		return children.get(index - 1);
	}

	public Node getCondition() {
		int index = (hasLetTypeDef() ? 1 : 0) + (hasCondition() ? 1 : 0);
		return children.get(index - 1);
	}

	public Node getShortBody() {
		int index = (hasLetTypeDef() ? 1 : 0) + (hasCondition() ? 1 : 0) + (hasShortBody() ? 1 : 0);
		return children.get(index - 1);
	}

	public ScopeBodyNode getBody() {
		return (ScopeBodyNode) children.getLast();
	}

}

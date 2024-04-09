package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.TokenType;

public class ReturnNode extends Node {
	
	public ReturnNode(TypeNode type, Node expr) {
		add(type);
		add(expr);
	}

	public Node getExpr() {
		return children.get(1);
	}

	public boolean returnsVoid() {
		return /*children.size() == 1 ||*/ getReturnType().getIdent().getType().equals(TokenType.VOID);
	}

	public TypeNode getReturnType() {
		return (TypeNode) children.get(0);
	}

}

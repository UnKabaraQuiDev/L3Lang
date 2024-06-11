package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class PackageDefNode extends Node {

	private Token token;
	private IdentifierLitNode value;

	public PackageDefNode(Token token, IdentifierLitNode identifierLitNode) {
		this.token = token;
		this.value = identifierLitNode;
	}

	public Token getToken() {
		return token;
	}

	public IdentifierLitNode getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString()+"('"+value+"' "+token.getPosition()+")";
	}

}

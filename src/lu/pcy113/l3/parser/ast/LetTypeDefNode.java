package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class LetTypeDefNode extends Node {

	private Token type;
	private IdentifierToken ident;
	private Node value;
	private boolean iStatic;

	public LetTypeDefNode(Token type, IdentifierToken ident, Node value, boolean iStatic) {
		this.type = type;
		this.ident = ident;
		this.value = value;
		this.iStatic = iStatic;
	}

	public Token getType() {
		return type;
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public Node getValue() {
		return value;
	}
	
	public boolean isiStatic() {
		return iStatic;
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+type.getType().getValue()+" "+ident.getIdentifier()+"="+value+")";
	}

}

package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.lexer.TokenType;

public class PrimitiveTypeNode extends TypeNode {

	private TokenType type;

	public PrimitiveTypeNode(TokenType ident) {
		this.type = ident;
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + type.toString() + ")";
	}

}

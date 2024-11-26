package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public class PrimitiveTypeNode extends TypeNode {

	private TokenType type;

	public PrimitiveTypeNode(Token token) {
		super(token.getType().name());
		this.type = token.getType();
	}
	
	public PrimitiveTypeNode(TokenType type) {
		super(type.name());
		this.type = type;
	}

	public TokenType getType() {
		return type;
	}

	public boolean isDouble() {
		return type.matches(TokenType.DOUBLE);
	}
	
	public boolean isFloat() {
		return type.matches(TokenType.FLOAT);
	}

	public boolean isInt() {
		return type.matches(TokenType.INT);
	}

	public boolean isBool() {
		return type.matches(TokenType.BOOLEAN);
	}

	public boolean isSigned() {
		return isInt() && type.name().endsWith("_S");
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("type", type.name());
	}

}

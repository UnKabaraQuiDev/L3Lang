package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class PrimitiveTypeNode extends TypeNode {

	private final TokenType type;

	public PrimitiveTypeNode(final Token token) {
		super(token.getType().name());
		this.type = token.getType();
	}

	public PrimitiveTypeNode(final TokenType type) {
		super(type.name());
		this.type = type;
	}

	@Override
	public int computeSize() {
		return NumericLiteralToken.NumericValueType.byTokenType(this.type).getBytes();
	}

	public TokenType getType() {
		return this.type;
	}

	public boolean isDouble() {
		return this.type.matches(TokenType.DOUBLE);
	}

	public boolean isFloat() {
		return this.type.matches(TokenType.FLOAT);
	}

	public boolean isInt() {
		return this.type.matches(TokenType.INT);
	}

	public boolean isBool() {
		return this.type.matches(TokenType.BOOLEAN);
	}

	public boolean isSigned() {
		return this.isInt() && this.type.name().endsWith("_S");
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("type", this.type.name());
	}

}

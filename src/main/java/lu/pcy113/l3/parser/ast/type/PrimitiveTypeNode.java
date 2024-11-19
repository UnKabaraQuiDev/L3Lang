package lu.pcy113.l3.parser.ast.type;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public class PrimitiveTypeNode extends TypeNode {

	private TokenType type;
	
	public PrimitiveTypeNode(Token token) {
		this.type = token.getType();
	}
	
	public TokenType getType() {
		return type;
	}
	
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("type", type.name());
	}

}

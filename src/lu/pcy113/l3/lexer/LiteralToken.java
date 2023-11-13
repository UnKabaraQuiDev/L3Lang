package lu.pcy113.l3.lexer;

import lu.pcy113.l3.parser.ValueType;

public abstract class LiteralToken<T> extends Token {
	
	public LiteralToken(TokenType type, int line, int column) {
		super(type, line, column);
	}
	
	public abstract T getValue();
	public abstract ValueType getValueType();
	
}

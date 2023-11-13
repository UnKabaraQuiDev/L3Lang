package lu.pcy113.l3.parser;

import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.LET;

import java.util.List;

import lu.pcy113.l3.lexer.IdentifierToken;
import lu.pcy113.l3.lexer.LiteralToken;
import lu.pcy113.l3.lexer.Token;
import lu.pcy113.l3.lexer.TokenType;

public class L3Parser {
	
	private int index;
	private final List<Token> input;

	public L3Parser(List<Token> tokens) {
		this.input = tokens;
	}
	
	private Token token = null;
	
	public void parse() throws ParserException {
		while(hasNext()) {
			token = consume();
			
			if(token.getType().equals(LET)/* &&
					peek().getType().equals(IDENT) &&
					peek(1).getType().equals(ASSIGN)*/) {
				parseVariableDeclaration();
			}
		}
	}
	
	private ExpressionContainer container;
	
	private void parseVariableDeclaration() throws ParserException {
		IdentifierToken varName = (IdentifierToken) needs(IDENT);
		Token assign = needs(ASSIGN);
		LiteralToken value = needsLiteral();
		container.add(new VariableVariableDeclaration(value.getValueType(), (varName.getIdentifier(), value.getValue()));
	}

	private LiteralToken needsLiteral() throws ParserException {
		Token t = consume();
		if(t instanceof LiteralToken) {
			return t;
		}
		throw new ParserException("Expected a value but got "+t);
	}
	private Token needs(TokenType ident) {
		Token t = consume();
		if(t.getType().equals(ident)) {
			return t;
		}
		throw new ParserException("Expected "+ident+" but got "+t);
	}

	public boolean hasNext() {
		return index < input.size();
	}
	public Token consume() {
		return consume(1);
	}
	public Token consume(int i) {
		Token c = input.get(index);
		index += i;
		return c;
	}
	public Token peek() {
		return peek(0);
	}
	public Token peek(int i) {
		return input.get(index+i);
	}
	
}

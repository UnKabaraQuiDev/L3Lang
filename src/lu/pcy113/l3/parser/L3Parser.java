package lu.pcy113.l3.parser;

import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.INTEGER;
import static lu.pcy113.l3.lexer.TokenType.MUL;
import static lu.pcy113.l3.lexer.TokenType.SEMICOLON;

import java.util.List;

import lu.pcy113.l3.lexer.IdentifierToken;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LiteralToken;
import lu.pcy113.l3.lexer.Token;
import lu.pcy113.l3.lexer.TokenType;

public class L3Parser {
	
	private int index;
	private final List<Token> input;
	
	public L3Parser(L3Lexer lexer) {
		this(lexer.getTokens());
	}
	public L3Parser(List<Token> tokens) {
		if(tokens == null || tokens.isEmpty())
			throw new IllegalArgumentException("Tokens cannot be null or empty.");
		
		this.input = tokens;
	}
	
	public void parse() throws ParserException {
		while(hasNext()) {
			
			if(peek(INTEGER)) {
				Token token = consume();
				parseVariableDeclaration(token.getType());
			}
			
		}
	}
	
	private ExpressionContainer container;
	
	private void parseVariableDeclaration(TokenType type) throws ParserException {
		IdentifierToken varName = (IdentifierToken) needs(IDENT);
		Token assign;
		//List<Token> value;
		
		if(peek(ASSIGN)) {
			assign = needs(ASSIGN);
			/*value = new ArrayList<Token>();
			while(!peek(SEMICOLON)) {
				value.add(consume());
			}*/
			parseMath();
			/*if(value.isEmpty())
				throw new ParserException("Expected a value but got "+peek());*/
		}
		
		switch(type) {
		case INTEGER:
			container.add(new IntegerVariableAllocation(varName.getIdentifier()));
			//if(value != null)
			//	container.add(new VariableSet(varNameOPEN_PARENT.getIdentifier(), new IntegerLiteral(value.getValue())));
			break;
		}
	}

	private EvaluableExpression parseMath() {
		EvaluableExpression expression = new EvaluableExpression();
		
		// TODO
		
		do {
			if(peek(MUL)) {
				Token left = consume(-1);
				Token right = consume(1);
			}
		}while(peek(SEMICOLON));
		
		return expression;
	}
	
	private LiteralToken needsLiteral() throws ParserException {
		Token t = consume();
		if(t instanceof LiteralToken) {
			return (LiteralToken) t;
		}
		throw new ParserException("Expected a value but got "+t);
	}
	private Token needs(TokenType ident) throws ParserException {
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
	private boolean peek(TokenType type) {
		return peek().getType().equals(type);
	}
	
}

package lu.pcy113.l3.lexer.impl;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public interface LexerIterator {

	boolean hasNext();

	boolean peek(TokenType type);
	
	boolean peek(TokenType... types);
	
	boolean peek(int i, TokenType type);
	
	boolean peek(int i, TokenType... types);

	TokenType peek();

	TokenType peek(int i);
	
	Token consume();

	Token consume(TokenType type);
	
	Token consume(TokenType... type);


}

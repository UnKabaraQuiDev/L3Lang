package lu.pcy113.l3.lexer.impl;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;

public interface LexerIterator {

	boolean hasNext();

	boolean peek(TokenType type);

	TokenType peek();

	TokenType peek(int i);

	Token consume();

	Token consume(TokenType type);

}

package lu.pcy113.l3.parser.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public final class ParserContext {

	private final List<Token> tokens = new ArrayList<>();
	private int position = 0;

	public ParserContext(final LexerIterator iterator) {
		while (iterator.hasNext()) {
			final Token token = iterator.consume();
			if (token.getType() != TokenType.COMMENT) {
				this.tokens.add(token);
			}
		}
	}

	public boolean hasNext() {
		return this.position < this.tokens.size();
	}

	public int position() {
		return this.position;
	}

	public void position(final int position) {
		if (position < 0 || position > this.tokens.size()) {
			throw new IllegalArgumentException("Invalid parser position: " + position);
		}
		this.position = position;
	}

	public Token token() {
		return this.token(0);
	}

	public Token token(final int offset) {
		final int index = this.position + offset;
		if (index < 0 || index >= this.tokens.size()) {
			return null;
		}
		return this.tokens.get(index);
	}

	public TokenType peek() {
		final Token token = this.token();
		return token == null ? null : token.getType();
	}

	public TokenType peek(final int offset) {
		final Token token = this.token(offset);
		return token == null ? null : token.getType();
	}

	public boolean peek(final TokenType type) {
		return ParserContext.matches(this.peek(), type);
	}

	public boolean peek(final int offset, final TokenType type) {
		return ParserContext.matches(this.peek(offset), type);
	}

	public boolean peek(final TokenType... types) {
		return Arrays.stream(types).anyMatch(this::peek);
	}

	public boolean peek(final int offset, final TokenType... types) {
		return Arrays.stream(types).anyMatch(type -> this.peek(offset, type));
	}

	public boolean accept(final TokenType type) {
		if (!this.peek(type)) {
			return false;
		}
		this.consume();
		return true;
	}

	public boolean accept(final TokenType... types) {
		if (!this.peek(types)) {
			return false;
		}
		this.consume();
		return true;
	}

	public Token consume() {
		if (!this.hasNext()) {
			throw new L3Exception("Unexpected end of file.");
		}
		return this.tokens.get(this.position++);
	}

	public Token consume(final TokenType type) {
		if (!this.peek(type)) {
			throw this.expected(type);
		}
		return this.consume();
	}

	public Token consume(final TokenType... types) {
		if (!this.peek(types)) {
			throw this.expected(types);
		}
		return this.consume();
	}

	public IdentifierToken consumeIdentifier() {
		return (IdentifierToken) this.consume(TokenType.IDENT);
	}

	public boolean peekIdentifier(final String value) {
		final Token token = this.token();
		return token instanceof final IdentifierToken identifier && identifier.getValue().equals(value);
	}

	public boolean acceptIdentifier(final String value) {
		if (!this.peekIdentifier(value)) {
			return false;
		}
		this.consume();
		return true;
	}

	public L3Exception expected(final TokenType... types) {
		final String expected = Arrays.stream(types).map(TokenType::name).collect(Collectors.joining(" or "));
		return new L3Exception("Expected " + expected + " but got " + this.describe(this.token()) + ".");
	}

	public L3Exception error(final String message) {
		return new L3Exception(message + " at " + this.describe(this.token()) + ".");
	}

	public String describe(final Token token) {
		if (token == null) {
			return "<eof>";
		}
		return token.getType().name() + "(" + token.getPosition() + ")";
	}

	public boolean sameLine(final int offset, final int line) {
		final Token token = this.token(offset);
		return token != null && token.getLine() == line;
	}

	public List<Token> consumeRemainingOnLine() {
		final List<Token> lineTokens = new ArrayList<>();
		final Token first = this.token();
		if (first == null) {
			return lineTokens;
		}
		final int line = first.getLine();
		while (this.hasNext() && this.token().getLine() == line) {
			lineTokens.add(this.consume());
		}
		return lineTokens;
	}

	public static boolean matches(final TokenType actual, final TokenType expected) {
		return actual != null && expected != null && actual.matches(expected);
	}
}

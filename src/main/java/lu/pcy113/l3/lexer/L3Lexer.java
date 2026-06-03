package lu.pcy113.l3.lexer;

import static lu.pcy113.l3.lexer.TokenType.AND;
import static lu.pcy113.l3.lexer.TokenType.ARROW;
import static lu.pcy113.l3.lexer.TokenType.AS;
import static lu.pcy113.l3.lexer.TokenType.BIN_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.BIT_AND;
import static lu.pcy113.l3.lexer.TokenType.BIT_AND_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIT_NOT;
import static lu.pcy113.l3.lexer.TokenType.BIT_NOT_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIT_OR;
import static lu.pcy113.l3.lexer.TokenType.BIT_OR_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIT_SHIFT_LEFT;
import static lu.pcy113.l3.lexer.TokenType.BIT_SHIFT_SIGNED_RIGHT;
import static lu.pcy113.l3.lexer.TokenType.BIT_SHIFT_UNSIGNED_RIGHT;
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR;
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BOOLEAN;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_OPEN;
import static lu.pcy113.l3.lexer.TokenType.BYTE;
import static lu.pcy113.l3.lexer.TokenType.CASE;
import static lu.pcy113.l3.lexer.TokenType.CHAR;
import static lu.pcy113.l3.lexer.TokenType.CHAR_LIT;
import static lu.pcy113.l3.lexer.TokenType.COLON;
import static lu.pcy113.l3.lexer.TokenType.COMMA;
import static lu.pcy113.l3.lexer.TokenType.COMMENT;
import static lu.pcy113.l3.lexer.TokenType.CURLY_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.CURLY_OPEN;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.DEFAULT;
import static lu.pcy113.l3.lexer.TokenType.DIV;
import static lu.pcy113.l3.lexer.TokenType.DIV_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.DOLLAR;
import static lu.pcy113.l3.lexer.TokenType.DOT;
import static lu.pcy113.l3.lexer.TokenType.DOUBLE;
import static lu.pcy113.l3.lexer.TokenType.ELSE;
import static lu.pcy113.l3.lexer.TokenType.EQUALS;
import static lu.pcy113.l3.lexer.TokenType.FALSE;
import static lu.pcy113.l3.lexer.TokenType.FINALLY;
import static lu.pcy113.l3.lexer.TokenType.FLOAT;
import static lu.pcy113.l3.lexer.TokenType.FOR;
import static lu.pcy113.l3.lexer.TokenType.FUN;
import static lu.pcy113.l3.lexer.TokenType.GREATER;
import static lu.pcy113.l3.lexer.TokenType.GREATER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.HASH;
import static lu.pcy113.l3.lexer.TokenType.HEX_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.IF;
import static lu.pcy113.l3.lexer.TokenType.IMPORT;
import static lu.pcy113.l3.lexer.TokenType.INT;
import static lu.pcy113.l3.lexer.TokenType.INT_1;
import static lu.pcy113.l3.lexer.TokenType.INT_16;
import static lu.pcy113.l3.lexer.TokenType.INT_32;
import static lu.pcy113.l3.lexer.TokenType.INT_64;
import static lu.pcy113.l3.lexer.TokenType.INT_8;
import static lu.pcy113.l3.lexer.TokenType.LESS;
import static lu.pcy113.l3.lexer.TokenType.LESS_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.LET;
import static lu.pcy113.l3.lexer.TokenType.LONG;
import static lu.pcy113.l3.lexer.TokenType.MINUS;
import static lu.pcy113.l3.lexer.TokenType.MINUS_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.MINUS_MINUS;
import static lu.pcy113.l3.lexer.TokenType.MODULO;
import static lu.pcy113.l3.lexer.TokenType.MODULO_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.MUL;
import static lu.pcy113.l3.lexer.TokenType.MUL_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.NEW;
import static lu.pcy113.l3.lexer.TokenType.NOT;
import static lu.pcy113.l3.lexer.TokenType.NOT_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.OR;
import static lu.pcy113.l3.lexer.TokenType.PACKAGE;
import static lu.pcy113.l3.lexer.TokenType.PAREN_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.PAREN_OPEN;
import static lu.pcy113.l3.lexer.TokenType.PLUS;
import static lu.pcy113.l3.lexer.TokenType.PLUS_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.PLUS_PLUS;
import static lu.pcy113.l3.lexer.TokenType.RETURN;
import static lu.pcy113.l3.lexer.TokenType.SEMICOLON;
import static lu.pcy113.l3.lexer.TokenType.SHORT;
import static lu.pcy113.l3.lexer.TokenType.STATIC;
import static lu.pcy113.l3.lexer.TokenType.STRICT_ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.STRING_LIT;
import static lu.pcy113.l3.lexer.TokenType.STRUCT;
import static lu.pcy113.l3.lexer.TokenType.SWITCH;
import static lu.pcy113.l3.lexer.TokenType.TRUE;
import static lu.pcy113.l3.lexer.TokenType.TYPE;
import static lu.pcy113.l3.lexer.TokenType.VOID;
import static lu.pcy113.l3.lexer.TokenType.WHILE;
import static lu.pcy113.l3.lexer.TokenType.XOR;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.CommentToken;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.utils.StringUtils;

public class L3Lexer {

	private int index = 0, line = 0, column = 0;
	private final String input;
	private final List<Token> tokens = new ArrayList<>();

	public L3Lexer(final String str) {
		this.input = str;
	}

	public L3Lexer(final Reader reader) throws IOException {
		this.input = StringUtils.readAll(reader);
	}

	private TokenType type = null;
	private String strValue = "";

	public void lexe() {
		while (this.hasNext()) {
			next: {
				final char current = this.consume();

				switch (current) {
				case '+':
					if (this.peek() == '=') {
						this.consume();
						this.type = PLUS_ASSIGN;
					} else if (this.peek() == '+') {
						this.consume();
						this.type = PLUS_PLUS;
					} else {
						this.type = PLUS;
					}
					this.flushToken();
					break next;
				case '-':
					if (this.peek() == '>') {
						this.consume();
						this.type = ARROW;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = MINUS_ASSIGN;
					} else if (this.peek() == '-') {
						this.consume();
						this.type = MINUS_MINUS;
					} else {
						this.type = MINUS;
					}
					this.flushToken();
					break next;
				case '*':
					if (this.peek() == '=') {
						this.consume();
						this.type = MUL_ASSIGN;
					} else {
						this.type = MUL;
					}
					this.flushToken();
					break next;
				case '/':
					if (this.peek() == '/') {
						this.type = COMMENT;
						this.strValue = "/";
						while (this.hasNext() && this.peek() != '\n') { // ignore ligne
							this.strValue += this.consume();
						}
						this.flushToken();
						break next;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = DIV_ASSIGN;
					} else {
						this.type = DIV;
					}
					this.flushToken();
					break next;

				case '(':
					this.type = PAREN_OPEN;
					this.flushToken();
					break next;
				case ')':
					this.type = PAREN_CLOSE;
					this.flushToken();
					break next;
				case '[':
					this.type = BRACKET_OPEN;
					this.flushToken();
					break next;
				case ']':
					this.type = BRACKET_CLOSE;
					this.flushToken();
					break next;
				case '{':
					this.type = CURLY_OPEN;
					this.flushToken();
					break next;
				case '}':
					this.type = CURLY_CLOSE;
					this.flushToken();
					break next;

				case '\"':
					this.type = STRING_LIT;
					this.strValue = "";

					int cl = this.line;
					int cc = this.column;

					// """ ... """ block string
					if (this.peek("\"\"")) {
						this.consume(2);

						while (this.hasNext() && !this.peek("\"\"\"")) {
							this.strValue += this.consume();
						}

						if (!this.hasNext()) {
							throw new LexerException("Unterminated string block, starting at: " + cl + ":" + cc);
						}

						this.consume(3);

						this.flushToken();
						break next;
					}

					// Normal "..." string
					while (this.hasNext() && this.peek() != '\"') {
						if (this.peek("\\")) {
							this.consume();

							if (this.peek('0', 'e', 'f', 'v', 'b', 't', 'n', 'r')) {
								final String strV = ("\\" + this.consume()).replace("\\n", "\n").replace("\\r", "\r")
										.replace("\\t", "\t").replace("\\b", "\b").replace("\\f", "\f")
										.replace("\\0", "\0");

								this.strValue += strV;
							}
						} else {
							this.strValue += this.consume();
						}
					}

					if (!this.hasNext()) {
						throw new LexerException("Unterminated string, starting at: " + cl + ":" + cc);
					}

					this.consume();
					this.flushToken();
					break next;

				case '\'':
					this.type = CHAR_LIT;
					cl = this.line;
					cc = this.column;
					this.strValue = this.consume() + "";
					if (!this.peek("'") || !this.hasNext()) {
						throw new LexerException("Unterminated string, starting at: " + cl + ":" + cc);
					}
					this.consume();
					this.flushToken();
					break next;

				case '$':
					this.type = DOLLAR;
					this.flushToken();
					break next;

				case ':':
					this.type = COLON;
					this.flushToken();
					break next;

				case ';':
					this.type = SEMICOLON;
					this.flushToken();
					break next;

				case ',':
					this.type = COMMA;
					this.flushToken();
					break next;

				case '.':
					this.type = DOT;
					this.flushToken();
					break next;

				case 'i':
					this.strValue = "i";
					if (this.peek("nt")) {
						this.consume(2);
						this.strValue += "nt";
					}
					if (this.peek("8")) {
						this.consume(1);
						this.type = INT_8;
						this.strValue += "8";
					} else if (this.peek("16")) {
						this.consume(2);
						this.type = INT_16;
						this.strValue += "16";
					} else if (this.peek("32")) {
						this.consume(2);
						this.type = INT_32;
						this.strValue += "32";
					} else if (this.peek("64")) {
						this.consume(2);
						this.type = INT_64;
						this.strValue += "64";
					} else if (this.peek("1")) {
						this.consume(2);
						this.type = INT_1;
						this.strValue += "1";
					} else if ("int".equals(this.strValue)) {
						this.type = INT;
					} else {
						this.checkOthers(current);
						break next;
					}
					if (this.type != null && this.type.matches(TYPE) && this.peek("s")) {
						this.consume();
						try {
							this.type = TokenType.valueOf(this.type.name() + "_S");
						} catch (final IllegalArgumentException e) {
							throw new LexerException(e, "Unknown variable type: " + this.strValue, this.line,
									this.column);
						}
					}
					if (this.type != null) {
						this.flushToken();
						break next;
					}
					break;

				case '|':
					if (this.peek() == '|') {
						this.consume();
						this.type = OR;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_OR_ASSIGN;
					} else {
						this.type = BIT_OR;
					}
					this.flushToken();
					break next;

				case '&':
					if (this.peek() == '&') {
						this.consume();
						this.type = AND;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_AND_ASSIGN;
					} else {
						this.type = BIT_AND;
					}
					this.flushToken();
					break next;

				case '%':
					if (this.peek() == '=') {
						this.consume();
						this.type = MODULO_ASSIGN;
					} else {
						this.type = MODULO;
					}
					this.flushToken();
					break next;

				case '#':
					this.type = HASH;
					this.flushToken();
					break next;

				case '!':
					if (this.peek() == '=') {
						this.consume();
						this.type = NOT_EQUALS;
					} else {
						this.type = NOT;
					}
					this.flushToken();
					break next;

				case '^':
					if (this.peek() == '^') {
						this.consume();
						this.type = XOR;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_XOR_ASSIGN;
					} else {
						this.type = BIT_XOR;
					}
					this.flushToken();
					break next;

				case '~':
					if (this.peek() == '=') {
						this.consume();
						this.type = BIT_NOT_ASSIGN;
					} else {
						this.type = BIT_NOT;
					}
					this.flushToken();
					break next;

				case '=':
					if (this.peek() == '=') {
						this.consume();
						this.type = EQUALS;
					} else {
						this.type = STRICT_ASSIGN;
					}
					this.flushToken();
					break next;
				case '<':
					if (this.peek() == '=') {
						this.consume();
						this.type = LESS_EQUALS;
					} else if (this.peek() == '<') {
						this.type = BIT_SHIFT_LEFT;
						this.consume();
					} else {
						this.type = LESS;
					}
					this.flushToken();
					break next;
				case '>':
					if (this.peek() == '=') {
						this.consume();
						this.type = GREATER_EQUALS;
					} else if (this.peek() == '>') {
						this.type = BIT_SHIFT_SIGNED_RIGHT;
						this.consume();
						if (this.peek() == '>') {
							this.type = BIT_SHIFT_UNSIGNED_RIGHT;
							this.consume();
						}
					} else {
						this.type = GREATER;
					}
					this.flushToken();
					break next;

				case ' ':
				case '\t':
				case '\n':
				case '\r':
					if (IDENT.equals(this.type) || NUM_LIT.equals(this.type) || DEC_NUM_LIT.equals(this.type)) {
						this.flushToken();
						break next;
					}
					break;

				case '0':
					if (this.peek() == 'x') {
						this.consume();
						this.strValue = "0x";
						this.type = HEX_NUM_LIT;
						do {
							this.strValue += this.consume();
						} while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_');
						this.flushToken();
						break next;
					} else if (this.peek() == 'b') {
						this.consume();
						this.strValue = "0b";
						this.type = BIN_NUM_LIT;
						do {
							this.strValue += this.consume();
						} while (this.peek() == '1' || this.peek() == '0' || this.peek() == '_');
						this.flushToken();
						break next;
					} else if (this.peek() == 'o') {
						this.consume();
						this.strValue = "0o";
						this.type = BIN_NUM_LIT;
						do {
							this.strValue += this.consume();
						} while (L3Lexer.isOctalDigit((char) this.peek()) || this.peek() == '_');
						this.flushToken();
						break next;
					}
				}

				this.checkOthers(current);
			}
		}
		// flushToken();
	}

	private void checkOthers(final char current) {
		if (this.type == null && Character.isLetter(current)) {
			this.type = IDENT;
			this.strValue = "" + current;
			while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_') {
				this.strValue += this.consume();
			}

			switch (this.strValue.toLowerCase()) {
			case "if":
				this.type = IF;
				break;
			case "else":
				this.type = ELSE;
				break;
			case "finally":
				this.type = FINALLY;
				break;
			case "for":
				this.type = FOR;
				break;
			case "while":
				this.type = WHILE;
				break;
			case "switch":
				this.type = SWITCH;
				break;
			case "case":
				this.type = CASE;
				break;
			case "default":
				this.type = DEFAULT;
				break;
			case "void":
				this.type = VOID;
				break;
			case "true":
				this.type = TRUE;
				break;
			case "false":
				this.type = FALSE;
				break;
			case "new":
				this.type = NEW;
				break;
			case "let":
				this.type = LET;
				break;
			case "fun":
				this.type = FUN;
				break;
			case "static":
				this.type = STATIC;
				break;
			case "return":
				this.type = RETURN;
				break;
			case "package":
				this.type = PACKAGE;
				break;
			case "import":
				this.type = IMPORT;
				break;
			case "as":
				this.type = AS;
				break;
			case "struct":
				this.type = STRUCT;
				break;
			case "byte":
				this.type = BYTE;
				break;
			case "short":
				this.type = SHORT;
				break;
			case "char":
				this.type = CHAR;
				break;
			case "long":
				this.type = LONG;
				break;
			case "float":
				this.type = FLOAT;
				break;
			case "double":
				this.type = DOUBLE;
				break;
			case "bool":
				this.type = BOOLEAN;
				break;
			}

			this.flushToken();
		} else if (this.type == null && Character.isDigit(current)) {
			this.type = NUM_LIT;
			this.strValue = "" + current;
			while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_' || this.peek() == '.'
					|| this.peek() == 'f') {
				this.strValue += this.consume();
			}
			if (this.strValue.contains(".") || this.strValue.contains("f")) {
				this.type = DEC_NUM_LIT;
			}
			this.flushToken();
		}
	}

	public void flushToken() {
		if (this.type == null) {
			return;
		}

		if (IDENT.equals(this.type)) {
			this.tokens.add(
					new IdentifierToken(this.type, this.line, this.column - this.strValue.length(), this.strValue));
		} else if (NUM_LIT.equals(this.type) || CHAR_LIT.equals(this.type) || DEC_NUM_LIT.equals(this.type)
				|| HEX_NUM_LIT.equals(this.type) || BIN_NUM_LIT.equals(this.type) || TRUE.equals(this.type)
				|| FALSE.equals(this.type)) {
			this.tokens.add(NumericLiteralToken.parseNumeric(this.type, this.line, this.column - this.strValue.length(),
					this.strValue));
		} else if (STRING_LIT.equals(this.type)) {
			this.tokens.add(
					new StringLiteralToken(this.type, this.line, this.column - this.strValue.length(), this.strValue));
		} else if (COMMENT.equals(this.type)) {
			this.tokens
					.add(new CommentToken(this.type, this.line, this.column - this.strValue.length(), this.strValue));
		} else {
			this.tokens.add(new Token(this.type, this.line, this.column - this.strValue.length()));
		}

		this.type = null;
		this.strValue = "";
	}

	public boolean hasNext() {
		return this.index < this.input.length();
	}

	public boolean hasNext(final int i) {
		return this.index + 1 < this.input.length();
	}

	public char consume() {
		return this.consume(1);
	}

	public char consume(final int i) {
		final char c = this.input.charAt(this.index);
		this.index += i;
		this.column++;
		if (c == '\n') {
			this.line++;
			this.column = 0;
		}
		return c;
	}

	public int peek() {
		return this.peek(0);
	}

	public void reverse() {
		this.index--;
	}

	public boolean peek(final String s) {
		boolean b = true;
		for (int i = 0; i < s.length(); i++) {
			if (this.peek(i) == s.charAt(i)) {
				continue;
			}
			b = false;
			break;
		}
		return b;
	}

	public boolean peek(final char... s) {
		final int c = this.peek();
		for (final char cs : s) {
			if (cs == c) {
				return true;
			}
		}
		return false;
	}

	public boolean peek(final int x, final char... s) {
		final int c = this.peek(x);
		for (final char cs : s) {
			if (cs == c) {
				return true;
			}
		}
		return false;
	}

	public boolean peek(final int x, final String s) {
		boolean b = true;
		for (int i = 0; i < s.length(); i++) {
			if (this.peek(i + x) == s.charAt(i)) {
				continue;
			}
			b = false;
			break;
		}
		return b;
	}

	public int peek(final int i) {
		return this.input.charAt(this.index + i);
	}

	// public int getIndex() {return index;}
	public String getInput() {
		return this.input;
	}

	public List<Token> getTokens() {
		return this.tokens;
	}

	public static boolean isOctalDigit(final char digit) {
		if (Character.isDigit(digit)) {
			final int numericValue = Character.getNumericValue(digit);
			return numericValue >= 0 && numericValue <= 7;
		}
		return false;
	}

	public LexerIterator iterator() {
		return new LexerIterator() {

			int pos = 0;

			@Override
			public TokenType peek(final int i) {
				return this.pos < L3Lexer.this.tokens.size() ? L3Lexer.this.tokens.get(this.pos + i).getType() : null;
			}

			@Override
			public TokenType peek() {
				return this.peek(0);
			}

			@Override
			public boolean peek(final TokenType type) {
				return this.peek().matches(type);
			}

			@Override
			public boolean peek(final int i, final TokenType type) {
				return this.peek(i).matches(type);
			}

			@Override
			public boolean hasNext() {
				return this.pos < L3Lexer.this.tokens.size();
			}

			@Override
			public Token consume(final TokenType type) {
//				GlobalLogger.log();

				if (this.peek(type)) {
					return this.consume();
				}

				throw new L3Exception("Expected: " + type + " but got: " + this.peek());
			}

			@Override
			public Token consume() {
//				GlobalLogger.log();

				return L3Lexer.this.tokens.get(this.pos++);
			}

			@Override
			public boolean peek(final TokenType... types) {
				return Arrays.stream(types).anyMatch(this::peek);
			}

			@Override
			public boolean peek(final int i, final TokenType... types) {
				return Arrays.stream(types).anyMatch(t -> this.peek(i, t));
			}

			@Override
			public Token consume(final TokenType... types) {
				if (this.peek(types)) {
					return this.consume();
				}
				throw new L3Exception("Expected: " + types + " but got: " + this.peek());
			}
		};
	}

}

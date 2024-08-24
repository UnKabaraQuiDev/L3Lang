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
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR;
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR_ASSIGN;
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
import java.util.List;

import lu.pcy113.l3.lexer.tokens.CommentToken;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.utils.StringUtils;

public class L3Lexer {

	private int index = 0, line = 0, column = 0;
	private final String input;
	private final List<Token> tokens;

	public L3Lexer(Reader reader) throws IOException {
		this.input = StringUtils.readAll(reader);
		this.tokens = new ArrayList<Token>();
	}

	private TokenType type = null;
	private String strValue = "";

	public void lexe() throws LexerException {
		while (hasNext()) {
			next: {
				char current = consume();

				switch (current) {
				case '+':
					if (peek() == '=') {
						consume();
						type = PLUS_ASSIGN;
					} else if (peek() == '+') {
						consume();
						type = PLUS_PLUS;
					} else {
						type = PLUS;
					}
					flushToken();
					break next;
				case '-':
					if (peek() == '>') {
						consume();
						type = ARROW;
					} else if (peek() == '=') {
						consume();
						type = MINUS_ASSIGN;
					} else if (peek() == '-') {
						consume();
						type = MINUS_MINUS;
					} else {
						type = MINUS;
					}
					flushToken();
					break next;
				case '*':
					if (peek() == '=') {
						consume();
						type = MUL_ASSIGN;
					} else {
						type = MUL;
					}
					flushToken();
					break next;
				case '/':
					if (peek() == '/') {
						type = COMMENT;
						strValue = "/";
						while (hasNext() && peek() != '\n') { // ignore ligne
							strValue += consume();
						}
						flushToken();
						break next;
					} else if (peek() == '=') {
						consume();
						type = DIV_ASSIGN;
					} else {
						type = DIV;
					}
					flushToken();
					break next;

				case '(':
					type = PAREN_OPEN;
					flushToken();
					break next;
				case ')':
					type = PAREN_CLOSE;
					flushToken();
					break next;
				case '[':
					type = BRACKET_OPEN;
					flushToken();
					break next;
				case ']':
					type = BRACKET_CLOSE;
					flushToken();
					break next;
				case '{':
					type = CURLY_OPEN;
					flushToken();
					break next;
				case '}':
					type = CURLY_CLOSE;
					flushToken();
					break next;

				case '\"':
					type = STRING_LIT;
					strValue = "";
					int cl = line, cc = column;
					while (hasNext() && peek() != '\"') {
						if (peek("\\")) {
							consume();
							if (peek('0', 'e', 'f', 'v', 'b', 't', 'n', 'r')) {
								String strV = ("\\" + consume()).replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").replace("\\b", "\b")
										// .replace("\\v", "\v")
										.replace("\\f", "\f")
										// .replace("\\e", "\e")
										.replace("\\0", "\0");
								strValue += strV;
							}
						} else {
							strValue += consume();
						}
					}
					if (!hasNext()) {
						throw new LexerException("Unterminated string, starting at: " + cl + ":" + cc);
					}
					consume();
					flushToken();
					break next;

				case '\'':
					type = CHAR_LIT;
					cl = line;
					cc = column;
					strValue = consume() + "";
					if (!peek("'") || !hasNext()) {
						throw new LexerException("Unterminated string, starting at: " + cl + ":" + cc);
					}
					consume();
					flushToken();
					break next;

				case '$':
					type = DOLLAR;
					flushToken();
					break next;

				case ':':
					type = COLON;
					flushToken();
					break next;

				case ';':
					type = SEMICOLON;
					flushToken();
					break next;

				case ',':
					type = COMMA;
					flushToken();
					break next;

				case '.':
					type = DOT;
					flushToken();
					break next;

				case 'i':
					strValue = "i";
					if (peek("nt")) {
						consume(2);
						strValue += "nt";
					}
					if (peek("8")) {
						consume(1);
						type = INT_8;
						strValue += "8";
					} else if (peek("16")) {
						consume(2);
						type = INT_16;
						strValue += "16";
					} else if (peek("32")) {
						consume(2);
						type = INT_32;
						strValue += "32";
					} else if (peek("64")) {
						consume(2);
						type = INT_64;
						strValue += "64";
					} else if (peek("1")) {
						consume(2);
						type = INT_1;
						strValue += "1";
					} else if (strValue.equals("int")) {
						type = INT;
					} else {
						checkOthers(current);
						break next;
					}
					if (type != null && type.matches(TYPE) && peek("s")) {
						consume();
						try {
							type = TokenType.valueOf(type.name() + "_S");
						} catch (IllegalArgumentException e) {
							throw new LexerException(e, "Unknown variable type: " + strValue, line, column);
						}
					}
					if (type != null) {
						flushToken();
						break next;
					}
					break;

				case '|':
					if (peek() == '|') {
						consume();
						type = OR;
					} else if (peek() == '=') {
						consume();
						type = BIT_OR_ASSIGN;
					} else {
						type = BIT_OR;
					}
					flushToken();
					break next;

				case '&':
					if (peek() == '&') {
						consume();
						type = AND;
					} else if (peek() == '=') {
						consume();
						type = BIT_AND_ASSIGN;
					} else {
						type = BIT_AND;
					}
					flushToken();
					break next;

				case '%':
					if (peek() == '=') {
						consume();
						type = MODULO_ASSIGN;
					} else {
						type = MODULO;
					}
					flushToken();
					break next;

				case '#':
					type = HASH;
					flushToken();
					break next;

				case '!':
					if (peek() == '=') {
						consume();
						type = NOT_EQUALS;
					} else {
						type = NOT;
					}
					flushToken();
					break next;

				case '^':
					if (peek() == '^') {
						consume();
						type = XOR;
					} else if (peek() == '=') {
						consume();
						type = BIT_XOR_ASSIGN;
					} else {
						type = BIT_XOR;
					}
					flushToken();
					break next;

				case '~':
					if (peek() == '=') {
						consume();
						type = BIT_NOT_ASSIGN;
					} else {
						type = BIT_NOT;
					}
					flushToken();
					break next;

				case '=':
					if (peek() == '=') {
						consume();
						type = EQUALS;
					} else {
						type = STRICT_ASSIGN;
					}
					flushToken();
					break next;
				case '<':
					if (peek() == '=') {
						consume();
						type = LESS_EQUALS;
					} else {
						type = LESS;
					}
					flushToken();
					break next;
				case '>':
					if (peek() == '=') {
						consume();
						type = GREATER_EQUALS;
					} else {
						type = GREATER;
					}
					flushToken();
					break next;

				case ' ':
				case '\t':
				case '\n':
				case '\r':
					if (IDENT.equals(type) || NUM_LIT.equals(type) || DEC_NUM_LIT.equals(type)) {
						flushToken();
						break next;
					}
					break;

				case '0':
					if (peek() == 'x') {
						consume();
						type = HEX_NUM_LIT;
						do {
							strValue += consume();
						} while (Character.isLetterOrDigit(peek()) || peek() == '_');
						flushToken();
						break next;
					} else if (peek() == 'b') {
						consume();
						type = BIN_NUM_LIT;
						do {
							strValue += consume();
						} while (peek() == '1' || peek() == '0' || peek() == '_');
						flushToken();
						break next;
					} else if (peek() == 'o') {
						consume();
						type = BIN_NUM_LIT;
						do {
							strValue += consume();
						} while (isOctalDigit((char) peek()) || peek() == '_');
						flushToken();
						break next;
					}
				}

				checkOthers(current);
			}
		}
		// flushToken();
	}

	private void checkOthers(char current) throws LexerException {
		if (type == null && Character.isLetter(current)) {
			type = IDENT;
			strValue = "" + current;
			while (Character.isLetterOrDigit(peek()) || peek() == '_') {
				strValue += consume();
			}

			switch (strValue.toLowerCase()) {
			case "if":
				type = IF;
				break;
			case "else":
				type = ELSE;
				break;
			case "finally":
				type = FINALLY;
				break;
			case "for":
				type = FOR;
				break;
			case "while":
				type = WHILE;
				break;
			case "switch":
				type = SWITCH;
				break;
			case "case":
				type = CASE;
				break;
			case "default":
				type = DEFAULT;
				break;
			case "void":
				type = VOID;
				break;
			case "true":
				type = TRUE;
				break;
			case "false":
				type = FALSE;
				break;
			case "new":
				type = NEW;
				break;
			case "let":
				type = LET;
				break;
			case "fun":
				type = FUN;
				break;
			case "static":
				type = STATIC;
				break;
			case "return":
				type = RETURN;
				break;
			case "package":
				type = PACKAGE;
				break;
			case "import":
				type = IMPORT;
				break;
			case "as":
				type = AS;
				break;
			case "struct":
				type = STRUCT;
				break;
			case "byte":
				type = BYTE;
				break;
			case "short":
				type = SHORT;
				break;
			case "char":
				type = CHAR;
				break;
			case "long":
				type = LONG;
				break;
			case "float":
				type = FLOAT;
				break;
			case "double":
				type = DOUBLE;
				break;
			}

			flushToken();
		} else if (type == null && Character.isDigit(current)) {
			type = NUM_LIT;
			strValue = "" + current;
			while (Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '.' || peek() == 'f') {
				strValue += consume();
			}
			if (strValue.contains(".") || strValue.contains("f")) {
				type = DEC_NUM_LIT;
			}
			flushToken();
		}
	}

	public void flushToken() throws LexerException {
		if (type == null)
			return;

		if (IDENT.equals(type)) {
			tokens.add(new IdentifierToken(type, line, column - strValue.length(), strValue));
		} else if (NUM_LIT.equals(type) || CHAR_LIT.equals(type) || DEC_NUM_LIT.equals(type) || HEX_NUM_LIT.equals(type) || BIN_NUM_LIT.equals(type)) {
			tokens.add(new NumericLiteralToken(type, line, column - strValue.length(), strValue));
		} else if (STRING_LIT.equals(type)) {
			tokens.add(new StringLiteralToken(type, line, column - strValue.length(), strValue));
		} else if (COMMENT.equals(type)) {
			tokens.add(new CommentToken(type, line, column - strValue.length(), strValue));
		} else {
			tokens.add(new Token(type, line, column - strValue.length()));
		}

		type = null;
		strValue = "";
	}

	public boolean hasNext() {
		return index < input.length();
	}

	public boolean hasNext(int i) {
		return index + 1 < input.length();
	}

	public char consume() {
		return consume(1);
	}

	public char consume(int i) {
		char c = input.charAt(index);
		index += i;
		column++;
		if (c == '\n') {
			line++;
			column = 0;
		}
		return c;
	}

	public int peek() {
		return peek(0);
	}

	public void reverse() {
		index--;
	}

	public boolean peek(String s) {
		boolean b = true;
		for (int i = 0; i < s.length(); i++) {
			if (peek(i) == s.charAt(i))
				continue;
			b = false;
			break;
		}
		return b;
	}

	public boolean peek(char... s) {
		int c = peek();
		for (char cs : s) {
			if (cs == c)
				return true;
		}
		return false;
	}

	public boolean peek(int x, char... s) {
		int c = peek(x);
		for (char cs : s) {
			if (cs == c)
				return true;
		}
		return false;
	}

	public boolean peek(int x, String s) {
		boolean b = true;
		for (int i = 0; i < s.length(); i++) {
			if (peek(i + x) == s.charAt(i)) {
				continue;
			}
			b = false;
			break;
		}
		return b;
	}

	public int peek(int i) {
		return input.charAt(index + i);
	}

	// public int getIndex() {return index;}
	public String getInput() {
		return input;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public static boolean isOctalDigit(char digit) {
		if (Character.isDigit(digit)) {
			int numericValue = Character.getNumericValue(digit);
			return numericValue >= 0 && numericValue <= 7;
		}
		return false;
	}

}

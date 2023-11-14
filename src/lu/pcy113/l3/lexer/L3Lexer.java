package lu.pcy113.l3.lexer;

import static lu.pcy113.l3.lexer.TokenType.AND;
import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIN_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.BIT_AND;
import static lu.pcy113.l3.lexer.TokenType.BIT_NOT;
import static lu.pcy113.l3.lexer.TokenType.BIT_OR;
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_OPEN;
import static lu.pcy113.l3.lexer.TokenType.CASE;
import static lu.pcy113.l3.lexer.TokenType.COMMA;
import static lu.pcy113.l3.lexer.TokenType.COMMENT;
import static lu.pcy113.l3.lexer.TokenType.CURLY_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.CURLY_OPEN;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.DEFAULT;
import static lu.pcy113.l3.lexer.TokenType.DIV;
import static lu.pcy113.l3.lexer.TokenType.ELSE;
import static lu.pcy113.l3.lexer.TokenType.EQUALS;
import static lu.pcy113.l3.lexer.TokenType.FALSE;
import static lu.pcy113.l3.lexer.TokenType.FINALLY;
import static lu.pcy113.l3.lexer.TokenType.FOR;
import static lu.pcy113.l3.lexer.TokenType.GREATER;
import static lu.pcy113.l3.lexer.TokenType.GREATER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.HEX_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.IF;
import static lu.pcy113.l3.lexer.TokenType.LOWER;
import static lu.pcy113.l3.lexer.TokenType.LOWER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.MINUS;
import static lu.pcy113.l3.lexer.TokenType.MODULO;
import static lu.pcy113.l3.lexer.TokenType.MUL;
import static lu.pcy113.l3.lexer.TokenType.NOT;
import static lu.pcy113.l3.lexer.TokenType.NOT_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.OR;
import static lu.pcy113.l3.lexer.TokenType.PAREN_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.PAREN_OPEN;
import static lu.pcy113.l3.lexer.TokenType.PLUS;
import static lu.pcy113.l3.lexer.TokenType.SEMICOLON;
import static lu.pcy113.l3.lexer.TokenType.STRING;
import static lu.pcy113.l3.lexer.TokenType.SWITCH;
import static lu.pcy113.l3.lexer.TokenType.TRUE;
import static lu.pcy113.l3.lexer.TokenType.VAR_1;
import static lu.pcy113.l3.lexer.TokenType.VAR_16;
import static lu.pcy113.l3.lexer.TokenType.VAR_32;
import static lu.pcy113.l3.lexer.TokenType.VAR_64;
import static lu.pcy113.l3.lexer.TokenType.VAR_8;
import static lu.pcy113.l3.lexer.TokenType.VOID;
import static lu.pcy113.l3.lexer.TokenType.WHILE;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.l3.lexer.tokens.CommentToken;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ParserException;
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
		/*TokenType[] fixedChars = (TokenType[]) Arrays.stream(TokenType.values())
				.filter(t -> t.isFixed() && !t.isString())
				.collect(Collectors.toList())
				.toArray();
		TokenType[] fixedStrings = (TokenType[]) Arrays.stream(TokenType.values())
				.filter(t -> t.isFixed() && t.isString())
				.collect(Collectors.toList())
				.toArray();*/
		
		while(hasNext()) {
			next: {
				char current = consume();
				//strValue = ""+current;
				
				System.out.println("Current: "+current+" type: "+type);
				
				/*if(DOUBLE_QUOTE.equals(type) && current != '\"') {
					strValue += current;
					continue;
				}*/
				
				switch (current) {
				case '+':
					type = PLUS;
					flushToken();
					break next;
				case '-':
					type = MINUS;
					flushToken();
					break next;
				case '*':
					type = MUL;
					flushToken();
					break next;
				case '/':
					if(peek() == '/') {
						type = COMMENT;
						strValue = "/";
						while(hasNext() && peek() != '\n') { // ignore ligne
							strValue += consume();
						}
						flushToken();
						break next;
					}
					type = DIV;
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
					/*if(DOUBLE_QUOTE.equals(type))
						flushToken();*/
					/*if(STRING.equals(type)) {
						flushToken();
						break next;
					}else
						type = STRING;*/
					type = STRING;
					strValue = "";
					int cl = line, cc = column;
					while(hasNext() && peek() != '\"') {
						strValue += consume();
					}
					if(!hasNext())
						throw new LexerException("Unterminated string, starting at: "+cl+":"+cc);
					consume();
					flushToken();
					break next;
					
				case ';':
					/*if(type != null)
						flushToken();*/
					type = SEMICOLON;
					flushToken();
					break next;
				
				case ',':
					type = COMMA;
					flushToken();
					break next;
					
				case 'v':
					strValue = "v";
					if(peek("ar")) {
						strValue += "ar";
						if(peek(2, "1")) {
							consume(3);
							type = VAR_1;
						}else if(peek(2, "8")) {
							consume(3);
							type = VAR_8;
						}else if(peek(2, "16")) {
							consume(4);
							type = VAR_16;
						}else if(peek(2, "32")) {
							consume(4);
							type = VAR_32;
						}else if(peek(2, "64")) {
							consume(4);
							type = VAR_64;
						}else {
							throw new LexerException("Unknown variable type: "+strValue, line, column);
						}
						if(peek("s")) {
							consume();
							try {
								type = TokenType.valueOf(type.name()+"_S");
							}catch(IllegalArgumentException e) {
								throw new LexerException(e, "Unknown variable type: "+strValue, line, column);
							}
						}
						if(type != null) {
							flushToken();
							break next;
						}
					}
					break;
					
				/*case 'i':
					if(peek() == 'f') {
						consume(1);
						type = IF;
						flushToken();
						break next;
					}
					break;
				case 'e':
					if(peek("lse")) {
						consume(3);
						type = ELSE;
						flushToken();
						break next;
					}
					break;
				case 'f':
					if(peek("inally")) {
						consume(6);
						type = FINALLY;
						flushToken();
						break next;
					}
					break;*/
				
				case '|':
					if(peek() == '|') {
						consume();
						type = OR;
					}else 
						type = BIT_OR;
					flushToken();
					break next;
					
				case '&':
					if(peek() == '&') {
						consume();
						type = AND;
					}else
						type = BIT_AND;
					flushToken();
					break next;
				
				case '%':
					type = MODULO;
					flushToken();
					break next;
					
				case '!':
					if(peek() == '=') {
						consume();
						type = NOT_EQUALS;
					}else
						type = NOT;
					flushToken();
					break next;
					
				case '^':
					type = BIT_XOR;
					flushToken();
					break next;
					
				case '~':
					type = BIT_NOT;
					flushToken();
					break next;
					
				case '=':
					if(peek() == '=') {
						consume();
						type = EQUALS;
					} else
						type = ASSIGN;
					flushToken();
					break next;
				case '<':
					if(peek() == '=') {
						consume();
						type = LOWER_EQUALS;
					} else
						type = LOWER;
					flushToken();
					break next;
				case '>':
					if(peek() == '=') {
						consume();
						type = GREATER_EQUALS;
					} else
						type = GREATER;
					flushToken();
					break next;
				
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					if(IDENT.equals(type) || NUM_LIT.equals(type) || DEC_NUM_LIT.equals(type)) {
						flushToken();
						break next;
					}
					break;
				
				/*case '.':
					if(NUM_LIT.equals(type)) {
						type = DEC_NUM_LIT;
					}
					strValue += current;
					break;*/
				
				case '0':
					if(peek() == 'x') {
						consume();
						type = HEX_NUM_LIT;
						do {
							strValue += consume();
						}while(Character.isLetterOrDigit(peek()) || peek() == '_');
						flushToken();
						break next;
					}else if(peek() == 'b') {
						consume();
						type = BIN_NUM_LIT;
						do {
							strValue += consume();
						}while(peek() == '1' || peek() == '0' || peek() == '_');
						flushToken();
						break next;
					}
				}
				
				if(type == null && Character.isLetter(current)) {
					type = IDENT;
					strValue = ""+current;
					while(Character.isLetterOrDigit(peek())) {
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
					}
					
					/*if(strValue.toLowerCase().startsWith("var") && strValue.length() > 3) {
						String sub = strValue.substring(3).toLowerCase();
						if(sub.startsWith("1")) {
							type = VAR_1;
						}else if(sub.startsWith("8")) {
							type = VAR_8;
						}else if(sub.startsWith("16")) {
							type = VAR_16;
						}else if(sub.startsWith("32")) {
							type = VAR_32;
						}else if(sub.startsWith("64")) {
							type = VAR_64;
						}
						if(sub.endsWith("s")) {
							type = TokenType.valueOf(type.name()+"_S");
						}
					}*/
					
					flushToken();
				}else if(type == null && Character.isDigit(current)) {
					type = NUM_LIT;
					strValue = ""+current;
					while(Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '.') {
						strValue += consume();
					}
					if(strValue.contains("."))
						type = DEC_NUM_LIT;
					flushToken();
				}
			}
		}
		//flushToken();
	}
	
	public void flushToken() throws LexerException {
		System.out.println("flushed: "+strValue+" "+type);
		if(type == null)
			return;
		
		if(IDENT.equals(type)) {
			tokens.add(new IdentifierToken(type, line, column-strValue.length(), strValue));
		}else if(NUM_LIT.equals(type) || DEC_NUM_LIT.equals(type) || HEX_NUM_LIT.equals(type) || BIN_NUM_LIT.equals(type)) {
			tokens.add(new NumericLiteralToken(type, line, column-strValue.length(), strValue));
		}else if(STRING.equals(type)) {
			tokens.add(new StringLiteralToken(type, line, column-strValue.length(), strValue));
		}else if(COMMENT.equals(type)) {
			tokens.add(new CommentToken(type, line, column-strValue.length(), strValue));
		}else {
			tokens.add(new Token(type, line, column-strValue.length()));
		}
		
		type = null;
		strValue = "";
	}
	
	public boolean hasNext() {
		return index < input.length();
	}
	public boolean hasNext(int i) {
		return index+1 < input.length();
	}
	public char consume() {
		return consume(1);
	}
	public char consume(int i) {
		char c = input.charAt(index);
		index += i;
		column++;
		if(c == '\n') {
			line++;
			column = 0;
		}
		return c;
	}
	public int peek() {
		return peek(0);
	}
	public boolean peek(String s) {
		boolean b = true;
		for(int i = 0; i < s.length(); i++) {
			if(peek(i) == s.charAt(i))
				continue;
			b = false;
			break;
		}
		return b;
	}
	public boolean peek(int x, String s) {
		boolean b = true;
		for(int i = 0; i < s.length(); i++) {
			if(peek(i+x) == s.charAt(i))
				continue;
			b = false;
			break;
		}
		return b;
	}
	public int peek(int i) {
		return input.charAt(index+i);
	}
	
	//public int getIndex() {return index;}
	public String getInput() {return input;}
	public List<Token> getTokens() {return tokens;}

}

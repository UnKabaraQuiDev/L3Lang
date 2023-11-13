package lu.pcy113.l3.lexer;

import static lu.pcy113.l3.lexer.TokenType.AND;
import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIN_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.BIT_AND;
import static lu.pcy113.l3.lexer.TokenType.BIT_NOT;
import static lu.pcy113.l3.lexer.TokenType.BIT_OR;
import static lu.pcy113.l3.lexer.TokenType.BIT_XOR;
import static lu.pcy113.l3.lexer.TokenType.BOOLEAN;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.BRACKET_OPEN;
import static lu.pcy113.l3.lexer.TokenType.CURLY_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.CURLY_OPEN;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.DIV;
import static lu.pcy113.l3.lexer.TokenType.DOUBLE_QUOTE;
import static lu.pcy113.l3.lexer.TokenType.ELSE;
import static lu.pcy113.l3.lexer.TokenType.EQUALS;
import static lu.pcy113.l3.lexer.TokenType.FINALLY;
import static lu.pcy113.l3.lexer.TokenType.GREATER;
import static lu.pcy113.l3.lexer.TokenType.GREATER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.HEX_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.IF;
import static lu.pcy113.l3.lexer.TokenType.INTEGER;
import static lu.pcy113.l3.lexer.TokenType.LONG;
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
import static lu.pcy113.l3.lexer.TokenType.SHORT;
import static lu.pcy113.l3.lexer.TokenType.STRING;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
			char current = consume();
			//strValue = ""+current;
			
			//System.out.println("Current: "+current+" type: "+type);
			
			if(DOUBLE_QUOTE.equals(type) && current != '\"') {
				strValue += current;
				continue;
			}
			
			switch (current) {
			case '+':
				type = PLUS;
				flushToken();
				break;
			case '-':
				type = MINUS;
				flushToken();
				break;
			case '*':
				type = MUL;
				flushToken();
				break;
			case '/':
				type = DIV;
				flushToken();
				break;
			
			case '(':
				type = PAREN_OPEN;
				flushToken();
				break;
			case ')':
				type = PAREN_CLOSE;
				flushToken();
				break;
			case '[':
				type = BRACKET_OPEN;
				flushToken();
				break;
			case ']':
				type = BRACKET_CLOSE;
				flushToken();
				break;
			case '{':
				type = CURLY_OPEN;
				flushToken();
				break;
			case '}':
				type = CURLY_CLOSE;
				flushToken();
				break;
			
			case '\"':
				if(DOUBLE_QUOTE.equals(type))
					flushToken();
				if(STRING.equals(type))
					flushToken();
				type = STRING;
				break;
				
			case ';':
				if(type != null)
					flushToken();
				type = SEMICOLON;
				flushToken();
				break;
				
			case 'b':
				if(peek("ool")) {
					consume(3);
					type = BOOLEAN;
					flushToken();
				}
				break;
			case 's':
				if(peek("hort")) {
					consume(4);
					type = SHORT;
					flushToken();
				}
				break;
			case 'l':
				if(peek("ong")) {
					consume(3);
					type = LONG;
					flushToken();
				}
				break;
				
			case 'i':
				if(peek("nt")) {
					consume(2);
					type = INTEGER;
					flushToken();
				}else if(peek() == 'f') {
					consume(1);
					type = IF;
					flushToken();
				}
				break;
			case 'e':
				if(peek("lse")) {
					consume(3);
					type = ELSE;
					flushToken();
				}
				break;
			case 'f':
				if(peek("inally")) {
					consume(6);
					type = FINALLY;
					flushToken();
				}
				break;
			
			case '|':
				if(peek() == '|') {
					consume();
					type = OR;
				}else 
					type = BIT_OR;
				flushToken();
				break;
				
			case '&':
				if(peek() == '&') {
					consume();
					type = AND;
				}else
					type = BIT_AND;
				flushToken();
				break;
			
			case '%':
				type = MODULO;
				flushToken();
				
			case '!':
				if(peek() == '=') {
					consume();
					type = NOT_EQUALS;
				}else
					type = NOT;
				flushToken();
				break;
				
			case '^':
				type = BIT_XOR;
				flushToken();
				break;
				
			case '~':
				type = BIT_NOT;
				flushToken();
				break;
				
			case '=':
				if(peek() == '=') {
					consume();
					type = EQUALS;
				} else
					type = ASSIGN;
				flushToken();
				break;
			case '<':
				if(peek() == '=') {
					consume();
					type = LOWER_EQUALS;
				} else
					type = LOWER;
				flushToken();
				break;
			case '>':
				if(peek() == '=') {
					consume();
					type = GREATER_EQUALS;
				} else
					type = GREATER;
				flushToken();
				break;
			
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				if(IDENT.equals(type) || NUM_LIT.equals(type) || DEC_NUM_LIT.equals(type)) {
					flushToken();
				}
				break;
			
			case '.':
				if(NUM_LIT.equals(type)) {
					type = DEC_NUM_LIT;
				}
				strValue += current;
				break;
			
			case '0':
				if(peek() == 'x') {
					consume();
					type = HEX_NUM_LIT;
					do {
						strValue += consume();
					}while(Character.isLetterOrDigit(peek()) || peek() == '_');
					flushToken();
					break;
				}else if(peek() == 'b') {
					consume();
					type = BIN_NUM_LIT;
					do {
						strValue += consume();
					}while(peek() == '1' || peek() == '0' || peek() == '_');
					flushToken();
					break;
				}
			default:
				if(type == null && Character.isLetter(current)) {
					type = IDENT;
					strValue = ""+current;
					while(Character.isLetterOrDigit(peek())) {
						strValue += consume();
					}
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
				break;
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
		}else {
			tokens.add(new Token(type, line, column-strValue.length()));
		}
		
		type = null;
		strValue = "";
	}
	
	public boolean hasNext() {
		return index < input.length();
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
	public int peek(int i) {
		return input.charAt(index+i);
	}
	
	//public int getIndex() {return index;}
	public String getInput() {return input;}
	public List<Token> getTokens() {return tokens;}

}

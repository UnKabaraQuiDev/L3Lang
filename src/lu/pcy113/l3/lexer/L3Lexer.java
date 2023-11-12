package lu.pcy113.l3.lexer;

import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.DIV;
import static lu.pcy113.l3.lexer.TokenType.EQUALS;
import static lu.pcy113.l3.lexer.TokenType.GREATER;
import static lu.pcy113.l3.lexer.TokenType.GREATER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.LET;
import static lu.pcy113.l3.lexer.TokenType.LOWER;
import static lu.pcy113.l3.lexer.TokenType.LOWER_EQUALS;
import static lu.pcy113.l3.lexer.TokenType.MINUS;
import static lu.pcy113.l3.lexer.TokenType.MUL;
import static lu.pcy113.l3.lexer.TokenType.NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.PLUS;
import static lu.pcy113.l3.lexer.TokenType.SEMICOLON;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.l3.utils.StringUtils;

public class L3Lexer implements Runnable {
	
	private int index = 0, line = 0, column = 0;
	private final String input;
	private final List<Token> tokens;
	
	public L3Lexer(Reader reader) throws IOException {
		this.input = StringUtils.readAll(reader);
		this.tokens = new ArrayList<Token>();
	}
	
	private TokenType type = null;
	private String strValue = "";
	
	@Override
	public void run() {
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
				
			case ';':
				if(type != null)
					flushToken();
				type = SEMICOLON;
				flushToken();
				break;
				
			case 'l':
				if(peek() == 'e' && peek(1) == 't') {
					consume(2);
					type = LET;
					flushToken();
				}
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
				/*if(IDENT.equals(type)) { // should never be
					flushToken();
					break;
				}*/
				if(NUM_LIT.equals(type)) {
					type = DEC_NUM_LIT;
				}
			default:
				if(type == null) {
					if(Character.isDigit(current)) {
						type = NUM_LIT;
					}else if(Character.isLetter(current)) {
						type = IDENT;
					}
				}
				strValue += current;
				break;
			}
			System.out.println(strValue);
		}
		//flushToken();
	}
	
	public void flushToken() {
		System.out.println("flushed: "+strValue+" "+type);
		
		if(IDENT.equals(type)) {
			tokens.add(new IdentifierToken(type, line, column-strValue.length(), strValue));
		}else if(NUM_LIT.equals(type) || DEC_NUM_LIT.equals(type)) {
			tokens.add(new NumericLiteralToken(type, line, column-strValue.length(), strValue));
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
	public int peek(int i) {
		return input.charAt(index+i);
	}
	
	//public int getIndex() {return index;}
	public String getInput() {return input;}
	public List<Token> getTokens() {return tokens;}

}

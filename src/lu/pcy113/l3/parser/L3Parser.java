package lu.pcy113.l3.parser;

import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIN_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.HEX_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.SEMICOLON;
import static lu.pcy113.l3.lexer.TokenType.VAR_1;
import static lu.pcy113.l3.lexer.TokenType.VAR_16;
import static lu.pcy113.l3.lexer.TokenType.VAR_16_S;
import static lu.pcy113.l3.lexer.TokenType.VAR_32;
import static lu.pcy113.l3.lexer.TokenType.VAR_32_S;
import static lu.pcy113.l3.lexer.TokenType.VAR_64;
import static lu.pcy113.l3.lexer.TokenType.VAR_64_S;
import static lu.pcy113.l3.lexer.TokenType.VAR_8;
import static lu.pcy113.l3.lexer.TokenType.VAR_8_S;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.IdentifierToken;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.NumericLiteralToken;
import lu.pcy113.l3.lexer.Token;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.expressions.ExprContainer;
import lu.pcy113.l3.parser.expressions.NumericLiteralExpr;
import lu.pcy113.l3.parser.expressions.VariableAssignmentExpr;
import lu.pcy113.l3.parser.expressions.VariableDeclarationExpr;
import lu.pcy113.l3.utils.MemorySize;

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
	
	private ExprContainer container;
	
	public void parse() throws ParserException {
		container = new ExprContainer();
		
		while(hasNext()) {
			
			if(peek(VAR_1, VAR_8, VAR_16, VAR_32, VAR_64, VAR_8_S, VAR_16_S, VAR_32_S, VAR_64_S)) {
				parseVariableDeclaration();
			}
			
		}
	}
	
	private void parseVariableDeclaration() throws ParserException {
		TokenType tokenType = consume().getType();
		IdentifierToken varName = (IdentifierToken) needs(IDENT);
		
		container.add(new VariableDeclarationExpr(new MemorySize(tokenType), varName.getIdentifier()));
		
		if(peek(1, ASSIGN)) {
			parseVariableAssignment();
		}else {
			consume();
			consume(SEMICOLON);
		}
	}

	private void parseVariableAssignment() throws ParserException {
		IdentifierToken varName = (IdentifierToken) consume(IDENT);
		Token assign = consume(ASSIGN);
		
		if(peek(NUM_LIT, HEX_NUM_LIT, BIN_NUM_LIT, DEC_NUM_LIT)) {
			 container.add(new VariableAssignmentExpr(varName.getIdentifier(), parseNumericLiteral()));
		}// TODO
		
		//consume();
		consume(SEMICOLON);
	}
	
	private NumericLiteralExpr parseNumericLiteral() throws ParserException {
		NumericLiteralToken token = (NumericLiteralToken) consume();
		return new NumericLiteralExpr(token.getValue().longValue());
	}
	/** 
	 * Needs next
	 */
	private Token needs(TokenType ident) throws ParserException {
		Token t = peek();
		if(t.getType().equals(ident)) {
			return t;
		}
		throw new ParserException("Expected "+ident+" but got "+t);
	}
	
	private boolean hasNext() {
		return index < input.size();
	}
	private boolean hasNext(int x) {
		return index+x < input.size();
	}
	
	private Token consume() throws ParserException {
		return consume(1);
	}
	private Token consume(int i) throws ParserException {
		end();
		Token c = input.get(index);
		index += i;
		return c;
	}
	private Token consume(TokenType t) throws ParserException {
		if(!hasNext())
			throw new ParserException("Expected "+t+" but got end of input.");
		
		if(peek(t)) {
			return consume();
		}else {
			throw new ParserException("Expected "+t+" but got "+peek());
		}
	}
	
	private void end() throws ParserException {
		if(!hasNext())
			throw new ParserException("Unexpected end of input.");
	}
	
	private Token peek() {
		return peek(0);
	}
	private Token peek(int i) {
		return input.get(index+i);
	}
	/*private Token getPeek(TokenType type) {
		return peek().getType().equals(type) ? peek() : null;
	}*/
	private boolean peek(TokenType type) {
		return peek().getType().equals(type);
	}
	private boolean peek(int x, TokenType type) {
		return peek(x).getType().equals(type);
	}
	private boolean peek(TokenType... types) {
		TokenType peek = peek().getType();
		return Arrays.stream(types).map(peek::equals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}
	private boolean peek(int x, TokenType... types) {
		TokenType peek = peek(x).getType();
		return Arrays.stream(types).map(peek::equals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}
	
	public ExprContainer getContainer() {return container;}
	
}

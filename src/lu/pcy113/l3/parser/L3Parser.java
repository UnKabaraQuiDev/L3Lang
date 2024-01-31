package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.Dispatch;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.Expr;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.RuntimeNode;

public class L3Parser {
	
	private int index;
	private final List<Token> input;
	
	private Node root;
	private Node current;
	
	public L3Parser(L3Lexer lexer) {
		this(lexer.getTokens());
	}
	public L3Parser(List<Token> tokens) {
		if(tokens == null || tokens.isEmpty())
			throw new IllegalArgumentException("Tokens cannot be null or empty.");
		
		this.input = tokens;
		
		this.root = new RuntimeNode();
		this.current = root;
	}
	
	public void parse(Node container) throws ParserException {
		while(hasNext()) {
			
			parseExpr(container);
			
		}
	}
	
	
	
	private void parseExpr() throws ParserException {
		Token token = peek();
		TokenType type = token.getType();
		
		switch (type) {
		case VAR_1:
		case VAR_8:
		case VAR_8_S:
		case VAR_16:
		case VAR_16_S:
		case VAR_32:
		case VAR_32_S:
		case VAR_64:
		case VAR_64_S:
			parseVarDeclaration();
			break;
		default:
			break;
		}
	}
	private void parseVarDeclaration() throws ParserException {
		Token t = consume();
		Token ident = consume(TokenType.IDENT);
		Token equals = consume(TokenType.ASSIGN);
		
		parseNumericExpr();
	}
	
	private void parseNumericExpr() {
		// FROM https://en.wikipedia.org/wiki/Operator-precedence_parser
	}
	
	private Node parseNumericExpr_1(Node lhs, int minPrec) throws ParserException {
		Token lookahead = peek();
		TokenType lookaheadType = lookahead.getType();
		while(isBinary(lookaheadType) && precedence(lookaheadType) >= minPrec) {
			Token op = consume();
			TokenType opType = op.getType();
			Node rhs = parsePrimaryNumericExpr();
			lookahead = peek();
			lookaheadType = lookahead.getType();
			while(isBinary(lookaheadType) && precedence(lookaheadType) > precedence(opType)) {
				rhs = parseNumericExpr_1(rhs, precedence(opType)+(precedence(lookaheadType) > precedence(opType) ? 1 : 0));
			}
			lhs = 
		}
		return lhs;
	}
	
	private Node parsePrimaryNumericExpr() {
		return null;
	}
	
	public static int precedence(TokenType type) throws ParserException {
		switch(type) {
		case ASSIGN:
			return 10;
		case PLUS:
		case MINUS:
			return 20;
		case MUL:
		case DIV:
			return 30;
		case NOT:
			return 40;
		default:
			throw new ParserException("Invalid operator", type);
		}
	}
	public static int rightPrecedence(TokenType type) throws ParserException {
		switch(type) {
		case ASSIGN:
			return 20;
		case PLUS:
		case MINUS:
			return 21;
		case MUL:
		case DIV:
			return 31;
		case NOT:
			return -1;
		default:
			throw new ParserException("Invalid operator", type);
		}
	}
	public static int nextPrecedence(TokenType type) throws ParserException {
		switch(type) {
		case ASSIGN:
			return 9;
		case PLUS:
		case MINUS:
			return 20;
		case MUL:
		case DIV:
			return 30;
		case NOT:
			return 40;
		default:
			throw new ParserException("Invalid operator", type);
		}
	}
	public static boolean isBinary(TokenType type) throws ParserException {
		switch(type) {
		case ASSIGN:
		case PLUS:
		case MINUS:
		case MUL:
		case DIV:
			return true;
		case NOT:
			return false;
		default:
			throw new ParserException("Invalid operator", type);
		}
	}
	
	/** 
	 * Needs next
	 */
	private Token needs(TokenType ident) throws ParserException {
		Token t = peek();
		if(t.getType().equals(ident)) {
			return t;
		}
		throw new ParserException(t, ident);
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
			throw new ParserException("Expected %s but got end of input.", t);
		
		if(peek(t)) {
			return consume();
		}else {
			throw new ParserException(peek(), t);
		}
	}
	private Token consume(TokenType... types) throws ParserException {
		Token peek = peek();
		if(Arrays.stream(types).filter(peek.getType()::equals).collect(Collectors.counting()) > 0)
			return consume();
		else
			throw new ParserException(peek, types);
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
	
}

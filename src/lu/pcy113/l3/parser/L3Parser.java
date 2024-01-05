package lu.pcy113.l3.parser;

import static lu.pcy113.l3.lexer.TokenType.ASSIGN;
import static lu.pcy113.l3.lexer.TokenType.BIN_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.COMMA;
import static lu.pcy113.l3.lexer.TokenType.CURLY_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.CURLY_OPEN;
import static lu.pcy113.l3.lexer.TokenType.DEC_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.HEX_NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.IDENT;
import static lu.pcy113.l3.lexer.TokenType.NEW;
import static lu.pcy113.l3.lexer.TokenType.NUM_LIT;
import static lu.pcy113.l3.lexer.TokenType.PAREN_CLOSE;
import static lu.pcy113.l3.lexer.TokenType.PAREN_OPEN;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.expressions.Expr;
import lu.pcy113.l3.parser.expressions.FunctionDeclarationExpr;
import lu.pcy113.l3.parser.expressions.NumericLiteralExpr;
import lu.pcy113.l3.parser.expressions.ValueExpr;
import lu.pcy113.l3.parser.expressions.VarAssignmentExpr;
import lu.pcy113.l3.parser.expressions.VarExpr;
import lu.pcy113.l3.parser.expressions.VariableAssignmentExpr;
import lu.pcy113.l3.parser.expressions.containers.ExprContainer;
import lu.pcy113.l3.parser.expressions.containers.FunctionBodyExprContainer;

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
	
	public void parse(ExprContainer container) throws ParserException {
		while(hasNext()) {
			
			parseExpr(container);
			
			parseNumericalExpression(container);
			
		}
	}
	
	private void parseExpr(ExprContainer container) throws ParserException {
		if(peek(VAR_1, VAR_8, VAR_16, VAR_32, VAR_64, VAR_8_S, VAR_16_S, VAR_32_S, VAR_64_S) &&
				peek(1, IDENT) &&
				(peek(2, SEMICOLON) || peek(2, ASSIGN))) { // primitive var declaration
			container.add(parsePrimitiveVarDeclaration());
		}
	}
	
	private VarExpr parsePrimitiveVarDeclaration() throws ParserException {
		Token var = consume(VAR_1, VAR_8, VAR_16, VAR_32, VAR_64, VAR_8_S, VAR_16_S, VAR_32_S, VAR_64_S);
		Token ident = consume(IDENT);
		
		if(peek(SEMICOLON)) {
			return new VarAssignmentExpr(var, ident);
		}else if(peek(2, ASSIGN)) {
			return new VarAssignmentExpr(var, ident, parseValueExpression());
		}
		throw new ParserException(peek(2), SEMICOLON, ASSIGN);
	}
	
	private ValueExpr parseValueExpression() throws ParserException {
		if(peek(NEW)) {
			// new instance
		}else if(false) {
			// function call on instance
		}else {
			// literal expression
			// TODO multiplication superiority
		}
		return null;
	}
	
	private void parseNumericalExpression(ExprContainer container) throws ParserException {
		/*if(peek(VAR_1, VAR_8, VAR_16, VAR_32, VAR_64, VAR_8_S, VAR_16_S, VAR_32_S, VAR_64_S) && peek(1, IDENT)) {
			if(peek(2, SEMICOLON) || peek(2, ASSIGN))
				container.addAll(parseVariableDeclaration());
			else if(peek(2, PAREN_OPEN))
				container.addAll(parseFunctionDeclaration());
			else
				throw new ParserException(peek(2), SEMICOLON, ASSIGN, PAREN_OPEN);
		}else if(peek(IDENT)) {
			container.add(parseVariableAssignment());
		}else if(peek(VOID, IDENT) && peek(1, IDENT)) { // function
			container.addAll(parseFunctionDeclaration());
		}else if(peek(COMMENT)) {
			consume();
		}else {
			throw new ParserException(peek(), VAR_1, VAR_8, VAR_16, VAR_32, VAR_64, VAR_8_S, VAR_16_S, VAR_32_S, VAR_64_S, IDENT, VOID, COMMENT);
		}*/
	}
	
	private Collection<Expr> parseFunctionDeclaration() throws ParserException {
		Collection<Expr> fe = new ArrayList<>();
		
		Token returnType = consume();
		IdentifierToken functionName = (IdentifierToken) consume(IDENT);
		
		List<Expr> params;
		
		consume(PAREN_OPEN);
		if(!peek(PAREN_CLOSE))
			params = parseParameterList();
		else
			params = new ArrayList<>();
		consume(PAREN_CLOSE);
		
		fe.add(new FunctionDeclarationExpr(returnType, functionName.getIdentifier(), params));
		
		if(peek(SEMICOLON)) {
			consume();
			return fe;
		}
		
		consume(CURLY_OPEN);
		FunctionBodyExprContainer body = new FunctionBodyExprContainer(returnType, functionName.getIdentifier(), params);
		fe.add(body);
		while(!peek(CURLY_CLOSE)) {
			parseExpr(body);
		}
		consume(CURLY_CLOSE);
		
		return fe;
	}
	
	private List<Expr> parseParameterList() throws ParserException {
		List<Expr> params = new ArrayList<>();
		while(true) {
			params.addAll(parseVariableDeclaration());
			
			if(peek(COMMA))
				consume();
			else
				break;
		}
		return params;
	}
	
	private VariableAssignmentExpr parseVariableAssignment() throws ParserException {
		IdentifierToken varName = (IdentifierToken) consume(IDENT);
		Token assign = consume(ASSIGN);
		
		if(peek(NUM_LIT, HEX_NUM_LIT, BIN_NUM_LIT, DEC_NUM_LIT)) {
			VariableAssignmentExpr varAssign = new VariableAssignmentExpr(varName.getIdentifier(), parseNumericLiteral());
			if(peek(SEMICOLON))
				consume(SEMICOLON);
			return varAssign;
		}// TODO
		
		//consume();
		//consume(SEMICOLON);
		
		return null;
	}
	
	private NumericLiteralExpr parseNumericLiteral() throws ParserException {
		NumericLiteralToken token = (NumericLiteralToken) consume(NUM_LIT, HEX_NUM_LIT, BIN_NUM_LIT, DEC_NUM_LIT);
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

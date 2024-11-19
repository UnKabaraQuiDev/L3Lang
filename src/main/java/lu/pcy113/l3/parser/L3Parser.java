package lu.pcy113.l3.parser;

import java.util.function.Supplier;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.BinaryExpression;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.logger.GlobalLogger;

public class L3Parser {

	private final String path;
	private final LexerIterator iterator;

	private FileNode file;

	public L3Parser(LexerIterator iterator, String path) {
		this.iterator = iterator;
		this.path = path;
	}

	public void parse() {
		GlobalLogger.log();
		
		file = parseFileNode();
	}

	private FileNode parseFileNode() {
		GlobalLogger.log();
		
		final FileNode file = new FileNode(this.path, PCUtils.getFileExtension(path));
		while (iterator.hasNext()) {
			file.addChild(parseExpression());
			iterator.consume(TokenType.SEMICOLON);
		}
		return file;
	}

	private Node parseExpression() {
		GlobalLogger.log();
		
		if (iterator.peek(TokenType.LET)) {
			return parseLetDef();
		}
		return parseAdditiveExpression();
	}

	private Node parseLetDef() {
		GlobalLogger.log();
		
		iterator.consume(TokenType.LET);
		final TypeNode type = parseType();
		final IdentifierNode identifier = parseSimpleIdent();
		if (iterator.peek(TokenType.STRICT_ASSIGN)) {
			iterator.consume(TokenType.STRICT_ASSIGN);
			final Node value = parseExpression();

			return new LetDefNode(type, identifier, value);
		}

		return new LetDefNode(type, identifier);
	}

	private TypeNode parseType() {
		GlobalLogger.log();
		
		if (iterator.peek(TokenType.PRIMITIVE_TYPE)) {
			return new PrimitiveTypeNode(iterator.consume(TokenType.PRIMITIVE_TYPE));
		}
		notImplemented();
		return null;
	}

	private Node parseBinaryExpression(Supplier<Node> leftProvider, TokenType... opTypes) {
		GlobalLogger.log();
		
		Node left = leftProvider.get();

		while (iterator.peek(opTypes)) {
			final TokenType op = iterator.consume(opTypes).getType();
			Node right = parseMultiplicativeExpression();

			left = new BinaryExpression(left, op, right);
		}
		
		return left;
	}

	private Node parseAdditiveExpression() {
		GlobalLogger.log();
		
		return parseBinaryExpression(this::parseMultiplicativeExpression, TokenType.PLUS, TokenType.MINUS);
	}

	private Node parseMultiplicativeExpression() {
		GlobalLogger.log();
		
		return parseBinaryExpression(this::parsePrimary, TokenType.MUL, TokenType.DIV);
	}

	private IdentifierNode parseSimpleIdent() {
		GlobalLogger.log();
		
		return new IdentifierNode(((IdentifierToken) iterator.consume(TokenType.IDENT)).getValue());
	}

	private Node parseParenthesizedExpression() {
		GlobalLogger.log();
		
		iterator.consume(TokenType.PAREN_OPEN);
		final Node expression = parseExpression();
		iterator.consume(TokenType.PAREN_CLOSE);
		return expression;
	}

	private Node parsePrimary() {
		GlobalLogger.log();
		
		switch (iterator.peek()) {
		case PAREN_OPEN:
			return parseParenthesizedExpression();
		case STRING_LIT:
			return new StringLiteralNode((StringLiteralToken) iterator.consume());
		case NUM_LIT:
			return new NumericLiteralNode((NumericLiteralToken) iterator.consume());
		}

		throw new L3Exception("Unexpected token: " + iterator.peek());
	}

	private void notImplemented() {
		throw new L3Exception("Not implemented: " + iterator.consume());
	}

	public FileNode getFile() {
		return file;
	}

}

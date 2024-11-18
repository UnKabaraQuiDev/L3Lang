package lu.pcy113.l3.parser;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.FileNode;
import lu.pcy113.l3.parser.ast.IdentifierNode;
import lu.pcy113.l3.parser.ast.TypeNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;

public class L3Parser {

	private LexerIterator iterator;

	private FileNode file;

	public L3Parser(LexerIterator iterator) {
		this.iterator = iterator;
	}

	public void parse() {
		file = parseFileNode();
	}

	private FileNode parseFileNode() {
		final FileNode file = new FileNode();
		while (iterator.hasNext()) {
			file.addChild(parseExpression());
			iterator.consume(TokenType.SEMICOLON);
		}
		return file;
	}

	private Node parseExpression() {
		if (iterator.peek(TokenType.LET)) {
			return parseLetDef();
		}
		return parsePrimary();
	}

	private Node parseLetDef() {
		iterator.consume(TokenType.LET);
		final TypeNode type = parseType();
		final IdentifierNode identifier = parseSimpleIdent();
		if(iterator.peek(TokenType.ASSIGN)) {
			iterator.consume(TokenType.ASSIGN);
			final Node value = parseExpression();
			
			return new LetDefNode(type, identifier, value);
		}
		
		return new LetDefNode(type, identifier);
	}

	private TypeNode parseType() {
		if (iterator.peek(TokenType.PRIMITIVE_TYPE)) {
			return new PrimitiveTypeNode(iterator.consume(TokenType.PRIMITIVE_TYPE));
		}
		notImplemented();
		return null;
	}

	private IdentifierNode parseSimpleIdent() {
		return new IdentifierNode(((IdentifierToken) iterator.consume(TokenType.IDENT)).getValue());
	}

	private Node parsePrimary() {
		switch (iterator.peek()) {
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

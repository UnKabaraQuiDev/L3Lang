package lu.pcy113.l3.parser;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.FileNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;

public class L3Parser {

	private LexerIterator iterator;

	private FileNode file;
	
	public L3Parser(LexerIterator iterator) {
		this.iterator = iterator;
	}
	
	public void parse() {
		file = parseFileNode();
	}

	
	/**
	 * File
	 * : Statement + Statement
	 * : Expression SEMICOLON
	 */
	private FileNode parseFileNode() {
		final FileNode file = new FileNode();
		while(iterator.hasNext()) {
			file.addChild(parseExpression());
			iterator.consume(TokenType.SEMICOLON);
		}
		return file;
	}

	private Node parseExpression() {
		return parsePrimary();
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

	public FileNode getFile() {
		return file;
	}
	
}

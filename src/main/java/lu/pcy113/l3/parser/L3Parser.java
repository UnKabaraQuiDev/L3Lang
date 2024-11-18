package lu.pcy113.l3.parser;

import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.parser.ast.Node;

public class L3Parser {

	private LexerIterator iterator;

	private Node root;
	
	public L3Parser(LexerIterator iterator) {
		this.iterator = iterator;
	}
	
	public void parse() {
		
	}

}

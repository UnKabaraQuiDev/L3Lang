package lu.pcy113.l3.parser;

import lu.pcy113.l3.lexer.impl.LexerIterator;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.rules.L3Grammar;
import lu.pcy113.l3.parser.rules.ParserContext;
import lu.pcy113.l3.parser.rules.RuleParser;

import lu.kbra.pclib.PCUtils;

public class L3Parser {

	private final String path;
	private final LexerIterator iterator;

	private FileNode file;

	public L3Parser(final LexerIterator iterator, final String path) {
		this.iterator = iterator;
		this.path = path == null ? "" : path;
	}

	public void parse() {
		final ParserContext context = new ParserContext(this.iterator);
		final RuleParser parser = new RuleParser(context, new L3Grammar());
		this.file = parser.parseFile(this.path, PCUtils.getFileName(this.path));
	}

	public FileNode getFile() {
		return this.file;
	}
}

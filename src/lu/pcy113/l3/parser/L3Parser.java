package lu.pcy113.l3.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.utils.FileUtils;

public class L3Parser {

	private int index;
	private final List<Token> input;

	private FileNode root;

	public L3Parser(String source, L3Lexer lexer) throws ParserException {
		input = lexer.getTokens();

		if (input == null || input.isEmpty())
			throw new ParserException("Tokens cannot be null or empty.");

		this.root = new FileNode(source.substring(0, source.lastIndexOf(".l3")).replace('/', '.'));
	}

	public void parse() throws ParserException {
		if (hasNext()) {
			parsePackageDefExpr(root);
		}

		while (hasNext()) {

			parseLineExpr(root);

		}
	}

	private void parseLineExpr(FileNode node) {
		
	}

	private void parsePackageDefExpr(FileNode root) throws ParserException {
		if (peek(TokenType.PACKAGE) && peek(1, TokenType.STRING_LIT)) {

			PackageDefNode packageDef = new PackageDefNode(consume(TokenType.PACKAGE), ((StringLiteralToken) consume(TokenType.STRING_LIT)).getValue());

			if (!root.getSource().replaceAll("^" + packageDef.getValue() + "\\.", "").equals(FileUtils.getExtension(root.getSource()))) {
				throw new ParserException("Package declaration not matching with file path: " + root.getSource() + " (" + root.getSource().replaceAll("^" + packageDef.getValue() + "\\.", "") + ") & " + packageDef.getValue());
			}

			root.add(packageDef);

			consume(TokenType.SEMICOLON);
		} else {
			throw new ParserException("Missing package declaration");
		}
	}

	private boolean hasNext() {
		return index < input.size();
	}

	private boolean hasNext(int x) {
		return index + x < input.size();
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
		if (!hasNext())
			throw new ParserException("Expected %s but got end of input at: " + peek(-1).getPosition() + "->" + peek().getPosition(), t);

		if (peek(t)) {
			return consume();
		} else {
			throw new ParserException(peek(), t);
		}
	}

	private Token consume(TokenType... types) throws ParserException {
		Token peek = peek();
		if (Arrays.stream(types).filter(peek.getType()::softEquals).collect(Collectors.counting()) > 0)
			return consume();
		else
			throw new ParserException(peek, types);
	}

	private void end() throws ParserException {
		if (!hasNext())
			throw new ParserException("Unexpected end of input.");
	}

	private Token peek() {
		return peek(0);
	}

	private Token peek(int i) {
		return input.get(index + i);
	}

	private boolean peek(TokenType type) {
		if (!hasNext())
			return false;
		return peek().getType().softEquals(type);
	}

	private boolean peek(int x, TokenType type) {
		if (!hasNext(x))
			return false;
		return peek(x).getType().softEquals(type);
	}

	private boolean peek(TokenType... types) {
		if (!hasNext())
			return false;
		TokenType peek = peek().getType();
		return Arrays.stream(types).map(peek::softEquals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	private boolean peek(int x, TokenType... types) {
		TokenType peek = peek(x).getType();
		return Arrays.stream(types).map(peek::softEquals).collect(Collectors.reducing((a, b) -> a || b)).orElse(false);
	}

	public FileNode getRoot() {
		return root;
	}

}

package lu.pcy113.l3.parser.ast.lit;

import java.util.LinkedList;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.Node;

public class IdentifierLitNode extends Node {

	private LinkedList<IdentifierToken> tokens;

	public IdentifierLitNode(IdentifierToken token) {
		tokens = new LinkedList<IdentifierToken>();
		tokens.add(token);
	}

	public LinkedList<IdentifierToken> getTokens() {
		return tokens;
	}

	public IdentifierLitNode append(IdentifierToken consume) {
		tokens.add(consume);
		return this;
	}

	public IdentifierToken getLeaf() {
		return tokens.getLast();
	}

	public IdentifierToken get(int index) {
		return tokens.get(index);
	}

	public int size() {
		return tokens.size();
	}

	@Override
	public String toString() {
		return super.toString() + "(" + tokens.stream().map((t) -> t.getValue()).collect(Collectors.joining(".")) + ")";
	}

}

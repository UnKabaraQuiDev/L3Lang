package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.StringLiteralToken;

public class StringLitNode extends Node implements ArrayInit {

	private int length;
	private StringLiteralToken string;
	
	public StringLitNode(StringLiteralToken stringToken) {
		this.string = stringToken;
		this.length = stringToken.getValue().length() + 1;
		
		for(int i = 0; i < length-1; i++) {
			add(new NumLitNode((int) string.getValue().charAt(i)));
		}
		add(new NumLitNode(0)); // null-terminatory
	}
	
	public StringLiteralToken getString() {
		return string;
	}
	
	@Override
	public boolean hasExpr() {
		return !children.isEmpty();
	}
	
	@Override
	public int getArraySize() {
		return length;
	}
	
	@Override
	public Node getExpr(int i) {
		return children.get(i);
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+string.getValue()+")";
	}

}

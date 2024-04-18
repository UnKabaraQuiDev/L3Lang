package lu.pcy113.l3.parser.ast;

import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.l3.utils.StringUtils;

public class StringLitNode extends Node implements ArrayInit {

	private int length, stackSize;
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
	
	public TypeNode getType() {
		return new TypeNode(true, TokenType.INT_8, true);
	}
	
	@Override
	public int getStackSize() {
		return stackSize;
	}
	
	@Override
	public void setStackSize(int i) {
		this.stackSize = i;
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
		return super.toString()+"('"+string.getValue()+"')";
	}
	
	@Override
	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		String ret = tab + toString();
		return ret;
	}

}

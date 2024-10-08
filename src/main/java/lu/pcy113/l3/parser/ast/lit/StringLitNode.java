package lu.pcy113.l3.parser.ast.lit;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.CharLiteralToken;
import lu.pcy113.l3.lexer.tokens.StringLiteralToken;
import lu.pcy113.l3.parser.ast.ArrayInit;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.type.PointerTypeNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.l3.utils.StringUtils;

public class StringLitNode extends ExprNode implements ArrayInit {

	private int length, stackSize;
	private StringLiteralToken string;

	public StringLitNode(StringLiteralToken stringToken) {
		this.string = stringToken;
		this.length = stringToken.getValue().length() + 1;

		for (int i = 0; i < length - 1; i++) {
			add(new IntegerNumLitNode((int) stringToken.getValue().charAt(i)));
		}
		add(new IntegerNumLitNode(0)); // null-terminator
	}

	@Deprecated
	public StringLitNode(CharLiteralToken charToken) {
		throw new RuntimeException("Deprecated");

		/*
		 * this.string = charToken; this.length = 1;
		 * 
		 * add(new NumLitNode((int) charToken.getValue()));
		 */
	}

	public StringLiteralToken getString() {
		return string;
	}

	public TypeNode getType() {
		return new PointerTypeNode(new PrimitiveTypeNode(TokenType.CHAR));
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
		return super.toString() + "('" + string.getValue() + "')";
	}

	@Override
	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		String ret = tab + toString();
		return ret;
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return false;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return false; // TODO or is it ?
	}

}

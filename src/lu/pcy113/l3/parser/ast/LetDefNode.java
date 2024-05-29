package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class LetDefNode extends Node {

	private IdentifierToken ident;
	private boolean iStatic, arg;
	private int stackIndex = 0, stackSize = 4;

	public LetDefNode(TypeNode type, IdentifierToken ident, boolean iStatic, boolean arg) {
		add(type);
		this.ident = ident;
		this.iStatic = iStatic;
		this.arg = arg;
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public boolean hasExpr() {
		return children.size() >= 2;
	}

	public Node getExpr() {
		return children.get(1);
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public boolean isiStatic() {
		return iStatic;
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public int getStackIndex() {
		return stackIndex;
	}

	public void setStackIndex(int stackIndex) {
		this.stackIndex = stackIndex;
	}

	public boolean isArg() {
		return arg;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.getValue() + ", " + getType().toString() + ", index=" + stackIndex + ", size=" + stackSize + ")";
	}

}

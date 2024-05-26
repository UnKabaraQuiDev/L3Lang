package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;

public class VarNumNode extends Node implements RecursiveArithmeticOp {

	private IdentifierToken mainIdent, childIdent;
	private boolean arrayOffset = false;

	public VarNumNode(IdentifierToken mainIdent) {
		this.mainIdent = mainIdent;
	}

	public VarNumNode(IdentifierToken mainIdent, Node offset) {
		add(offset);
		this.mainIdent = mainIdent;
		this.arrayOffset = true;
	}

	public VarNumNode(IdentifierToken mainIdent, IdentifierToken childIdent) {
		this.childIdent = childIdent;
		this.mainIdent = mainIdent;
	}

	public VarNumNode(IdentifierToken mainIdent, IdentifierToken childIdent, Node offset) {
		add(offset);
		this.childIdent = childIdent;
		this.mainIdent = mainIdent;
		this.arrayOffset = true;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isFloat();
	}
	
	@Override
	public boolean isInt() throws CompilerException {
		return getClosestContainer().getLetTypeDefDescriptor(this).getNode().getType().isInt();
	}

	public boolean hasChild() {
		return childIdent != null;
	}

	public boolean isArrayOffset() {
		return arrayOffset;
	}

	public boolean isPointer() {
		return children.size() == 1;
	}

	public Node getOffset() {
		return children.get(0);
	}

	public IdentifierToken getMainIdent() {
		return mainIdent;
	}

	public IdentifierToken getChildIdent() {
		return childIdent;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + mainIdent.getValue() + (childIdent == null ? "" : "." + childIdent.getValue()) + ", pointer=" + isPointer() + ", arrayOffset=" + arrayOffset + ")";
	}

}

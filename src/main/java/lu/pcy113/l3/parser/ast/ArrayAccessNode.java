package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;

public class ArrayAccessNode extends ExprNode implements RecursiveArithmeticOp {

	private IdentifierLitNode ident;

	public ArrayAccessNode(IdentifierLitNode ident, ExprNode offset) {
		add(offset);
		this.ident = ident;
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		return getClosestContainer().getLetDefDescriptor(ident).getNode().getType().isDecimal();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return getClosestContainer().getLetDefDescriptor(ident).getNode().getType().isInteger();
	}

	public Node getOffset() {
		return children.get(0);
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + ident.toString() + ", offset=" + getOffset() + ")";
	}

}

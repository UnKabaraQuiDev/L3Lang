package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;

public class LetRefNode extends ExprNode implements RecursiveArithmeticOp {

	public LetRefNode(FieldAccessNode node) {
		add(node);
	}

	public FieldAccessNode getNode() {
		return (FieldAccessNode) children.get(0);
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		// TODO
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		// TODO
		return false;
	}

}

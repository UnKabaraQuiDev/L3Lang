package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class PointerDerefNode extends ExprNode {

	public PointerDerefNode(Node node) {
		add(node);
	}
	
	public Node getNode() {
		return children.get(0);
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}
	
}

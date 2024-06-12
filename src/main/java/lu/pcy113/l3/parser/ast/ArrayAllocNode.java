package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class ArrayAllocNode extends ExprNode {

	public ArrayAllocNode(TypeNode type, NumLitNode numLitNode) {
		add(type);
		add(numLitNode);
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public TypeNode getArraySize() {
		return (TypeNode) children.get(1);
	}

	@Override
	public String toString() {
		return super.toString();
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

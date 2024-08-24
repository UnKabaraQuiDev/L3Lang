package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class UserTypeAllocNode extends ExprNode {

	public UserTypeAllocNode(TypeNode type) {
		add(type);
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public FunCallParamsNode getArgs() {
		return (FunCallParamsNode) children.get(1);
	}

	@Override
	public boolean isDouble() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		// TODO Auto-generated method stub
		return false;
	}

}

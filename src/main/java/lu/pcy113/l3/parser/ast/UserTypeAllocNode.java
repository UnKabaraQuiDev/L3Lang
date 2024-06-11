package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class UserTypeAllocNode extends ExprNode {

	public UserTypeAllocNode(TypeNode type) {
		add(type);
	}

	public TypeNode getType() {
		return (TypeNode) children.get(0);
	}

	public ConArgsValNode getArgs() {
		return (ConArgsValNode) children.get(1);
	}

}

package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.parser.ast.type.TypeNode;

public interface ArrayInit {

	TypeNode getType();
	
	int getArraySize();

	boolean hasExpr();

	Node getExpr(int i);

	int getStackSize();
	void setStackSize(int i);

}

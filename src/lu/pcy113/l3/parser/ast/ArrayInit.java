package lu.pcy113.l3.parser.ast;

public interface ArrayInit {

	TypeNode getType();
	
	int getArraySize();

	boolean hasExpr();

	Node getExpr(int i);

	int getStackSize();
	void setStackSize(int i);

}

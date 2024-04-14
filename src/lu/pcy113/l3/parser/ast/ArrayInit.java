package lu.pcy113.l3.parser.ast;

public interface ArrayInit {

	int getArraySize();

	boolean hasExpr();

	Node getExpr(int i);

}

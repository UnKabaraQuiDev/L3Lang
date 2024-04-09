package lu.pcy113.l3.parser.ast;

public class LetTypeSetNode extends Node {

	public LetTypeSetNode(Node let, Node expr) {
		add(let);
		add(expr);
	}

	public VarNumNode getLet() {
		return (VarNumNode) children.get(0);
	}

	public Node getExpr() {
		return children.get(1);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getLet().getIdent() + ")";
	}

}

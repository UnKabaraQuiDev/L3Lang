package lu.pcy113.l3.parser.ast;

public class LocalizingNode extends Node {

	public LocalizingNode(VarNumNode varNode) {
		add(varNode);
	}
	
	public VarNumNode getNode() {
		return (VarNumNode) children.get(0);
	}
	
}

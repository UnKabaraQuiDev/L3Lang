package lu.pcy113.l3.parser.ast;

public class FunArgDefNode extends Node {

	public FunArgDefNode(LetDefNode typeDefNode) {
		add(typeDefNode);
	}
	
	public LetDefNode getLet() {
		return (LetDefNode) children.get(0);
	}
	
	@Override
	public String toString() {
		return super.toString()+"("+getLet()+")";
	}

}

package lu.pcy113.l3.parser.ast;

public class FunArgDefNode extends Node {

	public FunArgDefNode(LetTypeDefNode typeDefNode) {
		add(typeDefNode);
	}
	
	public LetTypeDefNode getLet() {
		return (LetTypeDefNode) children.get(0);
	}

}

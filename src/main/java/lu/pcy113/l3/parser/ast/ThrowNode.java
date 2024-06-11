package lu.pcy113.l3.parser.ast;

public class ThrowNode extends Node implements ReturnSafeNode {

	public ThrowNode() {
		this._void = true;
	}

	public ThrowNode(Node expr) {
		add(expr);
		this._void = false;
	}

	public Node getExpr() {
		return children.get(0);
	}

	public boolean returnsVoid() {
		return _void;
	}

	public boolean hasExpr() {
		return !_void;
	}
	
	@Override
	public boolean isReturnSafe() {
		return true;
	}

}

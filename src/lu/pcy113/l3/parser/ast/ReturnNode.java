package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;

public class ReturnNode extends Node {

	private boolean _void;

	public ReturnNode() {
		this._void = true;
	}

	public ReturnNode(Node expr) {
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

	public FunDefNode getFunDefParent() throws CompilerException {
		Node parent = this;
		while (!(parent instanceof FunDefNode)) {
			parent = parent.parent;

			if (parent == null) {
				throw new CompilerException("Parent is null.");
			}
		}
		return (FunDefNode) parent;
	}

}

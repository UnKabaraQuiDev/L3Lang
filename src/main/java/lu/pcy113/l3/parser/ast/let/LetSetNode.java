package lu.pcy113.l3.parser.ast.let;

import org.json.JSONObject;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.Node;

public class LetSetNode extends Node {

	private final Node parent;
	private final Node expr;
	private final TokenType assignType;

	public LetSetNode(final Node parent, final Node expr, final TokenType assignType) {
		this.parent = parent;
		this.expr = expr;
		this.assignType = assignType;
	}

	public Node getParent() {
		return this.parent;
	}

	public Node getExpr() {
		return this.expr;
	}

	public TokenType getAssignType() {
		return this.assignType;
	}

	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject().put("expr", this.expr.toJSONObject()).put("assignType", this.assignType.name())
				.put("parent", this.parent.toJSONObject());
	}

}

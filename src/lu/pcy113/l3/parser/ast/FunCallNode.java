package lu.pcy113.l3.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.ast.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.utils.StringUtils;

public class FunCallNode extends ExprNode implements RecursiveArithmeticOp {

	private boolean preset = false;
	private IdentifierLitNode ident;

	private List<ExprNode> params = new ArrayList<>();

	public FunCallNode(IdentifierLitNode ident, boolean preset) {
		this.ident = ident;
		this.preset = preset;
	}

	public FunCallNode(IdentifierLitNode ident) {
		this.ident = ident;
	}

	@Override
	public boolean isFloat() throws L3Exception {
		return getClosestContainer().getFunDescriptor(this).getNode().getReturnType().isFloat();
	}

	@Override
	public boolean isInt() throws L3Exception {
		return getClosestContainer().getFunDescriptor(this).getNode().getReturnType().isInt();
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public boolean isPreset() {
		return preset;
	}

	public void addParam(ExprNode expr) {
		this.params.add(expr);
	}

	@Override
	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		String ret = tab + toString()
				+ (params != null && !params.isEmpty() ? ("[\n" + (params.stream().map(c -> c == null ? "null" : c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]") : "")
				+ (!isLeaf() ? "[\n" + (children.stream().map(c -> c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]" : "");;
		return ret;
	}

	public String toString() {
		return super.toString() + "(" + ident.toString() + ", " + (preset ? "preset" : "def") + ")";
	}

}

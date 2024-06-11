package lu.pcy113.l3.parser.ast;

import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.IdentifierLitNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.utils.StringUtils;

public class FunCallNode extends ExprNode implements RecursiveArithmeticOp {

	private boolean preset = false;
	private IdentifierLitNode ident;

	public FunCallNode(IdentifierLitNode ident, FunCallParamsNode params, boolean preset) {
		this.ident = ident;
		add(params);
		this.preset = preset;
	}

	public FunCallNode(IdentifierLitNode ident, FunCallParamsNode params) {
		this.ident = ident;
		add(params);
	}

	public boolean isPrimitive() throws CompilerException {
		return getClosestContainer().getFunDefDescriptor(this).getNode().getReturnType() instanceof PrimitiveTypeNode;
	}

	public boolean isNumber() throws CompilerException {
		return isPrimitive() && getClosestContainer().getFunDefDescriptor(this).getNode().getReturnType() instanceof PrimitiveTypeNode;
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		return isNumber() && ((PrimitiveTypeNode) getClosestContainer().getFunDefDescriptor(this).getNode().getReturnType()).isDecimal();
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return isNumber() && ((PrimitiveTypeNode) getClosestContainer().getFunDefDescriptor(this).getNode().getReturnType()).isInteger();
	}

	public IdentifierLitNode getIdent() {
		return ident;
	}

	public boolean isPreset() {
		return preset;
	}

	public FunCallParamsNode getParams() {
		return (FunCallParamsNode) children.get(0);
	}

	@Override
	public String toString(int indent) {
		String tab = StringUtils.repeat("\t", indent);
		FunCallParamsNode params = getParams();
		String ret = tab + toString() + (!params.isLeaf() ? ("[\n" + (params.getChildren().stream().map(c -> c == null ? "null" : c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]") : "")
				+ (!isLeaf() ? "[\n" + (children.stream().map(c -> c.toString(indent + 1)).collect(Collectors.joining(",\n"))) + "\n" + tab + "]" : "");
		;
		return ret;
	}

	public String toString() {
		return super.toString() + "(" + ident.toString() + ", " + (preset ? "preset" : "def") + ")";
	}

}

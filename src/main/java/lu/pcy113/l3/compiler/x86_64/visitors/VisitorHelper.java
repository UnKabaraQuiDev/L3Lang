package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;
import lu.pcy113.l3.parser.ast.type.CastNode;

public class VisitorHelper {

	public static void compute(final ListNode parent, final Node node, final String reg, final FileCompilerUnit fu) {
		if (node instanceof final NumericLiteralNode num) {
			NumericLiteralVisitor.visit(num, reg, fu);
		} else if (node instanceof final IdentifierNode ident) {
			IdentifierNodeVisitor.visit(ident, reg, parent, fu);
		} else if (node instanceof final BinaryExpressionNode bin) {
			BinaryExpressionVisitor.visit(bin, reg, parent, fu);
		} else if (node instanceof final FunCallNode funCall) {
			FunCallVisitor.visit(funCall, parent, fu);
		} else if (node instanceof final StringLiteralNode str) {
			StringLiteralVisitor.visit(str, reg, parent, fu);
		} else if (node instanceof final CastNode cast) {
			CastVisitor.visit(cast, reg, parent, fu);
		} else {
			fu.implement(node);
		}
	}

}

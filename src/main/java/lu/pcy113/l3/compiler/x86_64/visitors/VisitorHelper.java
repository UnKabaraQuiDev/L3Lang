package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;

public class VisitorHelper {

	public static void compute(ListNode parent, Node node, String reg, FileCompilerUnit fu) {
		if (node instanceof NumericLiteralNode num) {
			NumericLiteralVisitor.visit(num, reg, fu);
		} else if (node instanceof IdentifierNode ident) {
			IdentifierNodeVisitor.visit(ident, reg, parent, fu);
		} else if (node instanceof BinaryExpressionNode bin) {
			BinaryExpressionVisitor.visit(bin, reg, parent, fu);
		} else if (node instanceof FunCallNode funCall) {
			FunCallVisitor.visit(funCall, parent, fu);
		} else {
			fu.implement(node);
		}
	}
	
}

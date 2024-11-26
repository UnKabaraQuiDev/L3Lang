package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;

public class FunDefVisitor {

	public static final String[] REGISTER_ORDER = { "rdi", "rsi", "rdx", "rcx", "r8", "r9" };

	public static void visit(FunDefNode fun, FileNode file, FileCompilerUnit fu) {
		fu.writeln(fun.name() + ":");
		fu.writeinstln("push rbp", "Save caller stackframe");
		fu.writeinstln("mov rbp, rsp", "Set new stackframe");

		for (int i = 0; i < fun.getArgs().size(); i++) {
			if (i > REGISTER_ORDER.length) {
				fun.getSymbols().get(fun.getArgs().get(i)).stack(i); // needs rework (size)
				throw new RuntimeException();
			}
			fun.getSymbols().get(fun.getArgs().get(i)).register(REGISTER_ORDER[i]);
		}

		for (Node n : fun.getChildren()) {
			if (n instanceof ReturnNode ret) {
				Node expr = ret.getExpression();
				if (expr instanceof NumericLiteralNode num) {
					NumericLiteralVisitor.visit(num, "rax", fu);
				} else if (expr instanceof IdentifierNode ident) { // if var/arg
					IdentifierNodeVisitor.visit(ident, "rax", fun, fu);;
				} else if (expr instanceof FunCallNode funCall) {
					FunCallVisitor.visit(funCall, fun, fu);
				}
				FunDefVisitor.return_(fun, file, fu);
			} else {
				throw new RuntimeException();
			}
		}

		// FunDefVisitor.return_(fun, file, fu);
	}

	private static void return_(FunDefNode fun, FileNode file, FileCompilerUnit fu) {
		fu.writeinstln("pop rbp", "Restore caller stackframe");
		fu.writeinstln("ret");
	}

}

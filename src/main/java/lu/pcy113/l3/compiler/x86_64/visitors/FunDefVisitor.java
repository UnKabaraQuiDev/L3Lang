package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.let.LetSetNode;
import lu.pcy113.l3.parser.ast.symbols.FunDefSymbol;

import lu.kbra.pclib.logger.GlobalLogger;

public class FunDefVisitor {

	public static final String[] REGISTER_ORDER = { "rdi", "rsi", "rdx", "rcx", "r8", "r9" };

	public static void visit(final FunDefNode fun, final FileNode file, final FileCompilerUnit fu) {
		final FunDefSymbol funSymbol = file.getSymbols().get(fun);

		GlobalLogger.log("Compiling: " + funSymbol.name());

		fu.writeln(funSymbol.name() + ":");
		fu.writeinstln("push rbp", "Save caller stackframe");
		fu.writeinstln("mov rbp, rsp", "Set new stackframe");

		for (int i = 0; i < fun.getArgs().size(); i++) {
			if (i > FunDefVisitor.REGISTER_ORDER.length) {
				fun.getSymbols().get(fun.getArgs().get(i)).stack(i); // needs rework (size)
				throw new RuntimeException();
			}

			fun.getSymbols().get(fun.getArgs().get(i)).register(FunDefVisitor.REGISTER_ORDER[i]);
		}

		for (final Node n : fun.getChildren()) {
			if (n instanceof final ReturnNode ret) {
				if (ret.hasExpression()) {
					final Node expr = ret.getExpression();
					VisitorHelper.compute(fun, expr, "rax", fu);
				}
				FunDefVisitor.return_(fun, file, fu);
			} else if (n instanceof final LetDefNode letDef) {
				LetDefVisitor.visit(letDef, fun, fu);
			} else if (n instanceof final FunCallNode funCall) {
				FunCallVisitor.visit(funCall, fun, fu);
			} else if (n instanceof final LetSetNode letSet) {
				LetSetVisitor.visit(letSet, fun, fu);
			} else {
				fu.implement(n);
			}
		}

		// FunDefVisitor.return_(fun, file, fu);
	}

	private static void return_(final FunDefNode fun, final FileNode file, final FileCompilerUnit fu) {
		fu.writeinstln("mov rsp, rbp", "Cleanup current stackframe");
		fu.writeinstln("pop rbp", "Restore caller stackframe");
		fu.writeinstln("ret");
	}

}

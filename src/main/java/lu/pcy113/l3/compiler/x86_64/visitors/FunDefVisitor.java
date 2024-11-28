package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.fun.ctrl.ReturnNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.logger.GlobalLogger;

public class FunDefVisitor {

	public static final String[] REGISTER_ORDER = { "rdi", "rsi", "rdx", "rcx", "r8", "r9" };

	public static void visit(FunDefNode fun, FileNode file, FileCompilerUnit fu) {
		GlobalLogger.log("Compiling: " + fun.name());

		fu.writeln(fun.name() + ":");
		fu.writeinstln("push rbp", "Save caller stackframe");
		fu.writeinstln("mov rbp, rsp", "Set new stackframe");

		int currentStackOffset = 0;

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
				VisitorHelper.compute(fun, expr, "rax", fu);
				FunDefVisitor.return_(fun, file, fu);
			} else if (n instanceof LetDefNode letDef) {
				TypeNode type = letDef.getType();
				int byteCount = fixStackOffset(type.computeSize());

				currentStackOffset += byteCount;

				fun.getSymbols().get(letDef).stack(currentStackOffset);

				if (letDef.hasValue()) {
					VisitorHelper.compute(fun, letDef.getValue(), "rax", fu);
					fu.writeinstln("push rax");
				} else {
					fu.writeinstln("sub rsp, " + byteCount);
				}
			} else {
				fu.implement(n);
			}
		}

		// FunDefVisitor.return_(fun, file, fu);
	}

	private static int fixStackOffset(int byteSize) {
		return byteSize < 8 ? 8 : PCUtils.snap(byteSize, 8);
	}

	private static void return_(FunDefNode fun, FileNode file, FileCompilerUnit fu) {
		fu.writeinstln("mov rsp, rbp", "Cleanup current stackframe");
		fu.writeinstln("pop rbp", "Restore caller stackframe");
		fu.writeinstln("ret");
	}

}

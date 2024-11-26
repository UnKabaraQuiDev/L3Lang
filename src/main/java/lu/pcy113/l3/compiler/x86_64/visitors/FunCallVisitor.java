package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;

public class FunCallVisitor {

	public static void visit(FunCallNode funCall, FunDefNode parent, FileCompilerUnit fu) {
		for (int i = 0; i < funCall.getArgs().size(); i++) {
			final Node arg = funCall.getArgs().get(i);

			VisitorHelper.compute(parent, arg, FunDefVisitor.REGISTER_ORDER[i], fu);
		}

		fu.writeinstln("call " + parent.getSymbols().get(funCall).name());
	}

}

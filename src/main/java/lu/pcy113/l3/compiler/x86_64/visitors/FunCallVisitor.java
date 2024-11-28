package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.fun.FunDefNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.symbols.FunDefSymbol;

public class FunCallVisitor {

	public static void visit(FunCallNode funCall, FunDefNode parent, FileCompilerUnit fu) {
		for (int i = 0; i < funCall.getArgs().size(); i++) {
			final Node arg = funCall.getArgs().get(i);

			VisitorHelper.compute(parent, arg, FunDefVisitor.REGISTER_ORDER[i], fu);
		}
		
		fu.writeinstln("call " + resolveASMName(funCall, parent));
	}

	private static String resolveASMName(FunCallNode funCall, FunDefNode parent) {
		Node caller = funCall.getParent();
		if(caller instanceof IdentifierNode ident) {
			return parent.getSymbols().<FunDefSymbol>getSymbol(ident.getValue()).name();
		}
		return null;
	}

}

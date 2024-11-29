package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.let.MembersAccess;
import lu.pcy113.l3.parser.ast.symbols.FileSymbol;
import lu.pcy113.l3.parser.ast.symbols.FunDefSymbol;
import lu.pcy113.l3.parser.ast.symbols.ImportSymbol;

public class FunCallVisitor {

	public static void visit(FunCallNode funCall, ListNode parent, FileCompilerUnit fu) {
		for (int i = 0; i < funCall.getArgs().size(); i++) {
			final Node arg = funCall.getArgs().get(i);

			VisitorHelper.compute(parent, arg, FunDefVisitor.REGISTER_ORDER[i], fu);
		}

		final String asmName = resolveASMName(funCall, parent);

		fu.writetextln("extern " + asmName);

		fu.writeinstln("call " + asmName);
	}

	private static String resolveASMName(FunCallNode funCall, ListNode parent) {
		Node caller = funCall.getParent();
		if (caller instanceof IdentifierNode ident) {
			return parent.getSymbols().<FunDefSymbol>getSymbol(ident.getValue()).name();
		} else if (caller instanceof MembersAccess access) {
			if (parent.getSymbols().contains(((IdentifierNode) access.getParent()).getValue())) { // file access (needs rework)
				ImportNode in = parent.getSymbols().<ImportSymbol>getSymbol(((IdentifierNode) access.getParent()).getValue()).getNode();
				FileNode fn = parent.getSymbols().<FileSymbol>getSymbol(in.getValue()).getNode();

				return fn.getSymbols().<FunDefSymbol>getSymbol(access.getProp().getValue()).name();
			}
		}

		throw new RuntimeException();
	}

}

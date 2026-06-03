package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.fun.FunCallNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.ident.ImportNode;
import lu.pcy113.l3.parser.ast.let.MembersAccess;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;
import lu.pcy113.l3.parser.ast.symbols.FileSymbol;
import lu.pcy113.l3.parser.ast.symbols.FunDefSymbol;
import lu.pcy113.l3.parser.ast.symbols.ImportSymbol;

import lu.kbra.pclib.datastructure.pair.Pairs;
import lu.kbra.pclib.datastructure.pair.ReadOnlyPair;

public class FunCallVisitor {

	public static void visit(final FunCallNode funCall, final ListNode parent, final FileCompilerUnit fu) {

		if (funCall.isPreset()) {
			FunCallVisitor.handlePreset(funCall, parent, fu);
			return;
		}

		for (int i = 0; i < funCall.getArgs().size(); i++) {
			final Node arg = funCall.getArgs().get(i);

			VisitorHelper.compute(parent, arg, FunDefVisitor.REGISTER_ORDER[i], fu);
		}

		final ReadOnlyPair<Boolean, FunDefSymbol> funDefRet = FunCallVisitor.resolveASMName(funCall, parent);
		final FunDefSymbol funDefSymbol = funDefRet.getValue();
		final boolean internal = funDefRet.getKey();

		if (!internal) {
			fu.writetextln("extern " + funDefSymbol.name());
		}

		fu.writeinstln("call " + funDefSymbol.name());
	}

	private static void handlePreset(final FunCallNode funCall, final ListNode parent, final FileCompilerUnit fu) {
		final String name = ((IdentifierNode) funCall.getParent()).getValue();

		if ("asm".equals(name)) {
			fu.writeinstln(((StringLiteralNode) funCall.getArgs().get(0)).getValue().getValue());
		} else if ("asmln".equals(name)) {
			fu.writeln(((StringLiteralNode) funCall.getArgs().get(0)).getValue().getValue());
		}
	}

	private static ReadOnlyPair<Boolean, FunDefSymbol> resolveASMName(final FunCallNode funCall, final ListNode parent) {
		final Node caller = funCall.getParent();
		if (caller instanceof final IdentifierNode ident) {
			return Pairs.readOnly(true, parent.getSymbols().<FunDefSymbol>getSymbol(ident.getValue()));
		} else if ((caller instanceof final MembersAccess access)
				&& parent.getSymbols().contains(((IdentifierNode) access.getParent()).getValue())) { // file access
																										// (needs
			// rework)
			final ImportNode in = parent.getSymbols()
					.<ImportSymbol>getSymbol(((IdentifierNode) access.getParent()).getValue()).getNode();
			final FileNode fn = parent.getSymbols().<FileSymbol>getSymbol(in.getValue()).getNode();

			return Pairs.readOnly(false, fn.getSymbols().<FunDefSymbol>getSymbol(access.getProp().getValue()));
		}

		throw new RuntimeException();
	}

}

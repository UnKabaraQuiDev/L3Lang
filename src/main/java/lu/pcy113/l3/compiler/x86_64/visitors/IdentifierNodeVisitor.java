package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.symbols.LetDefSymbol;

public class IdentifierNodeVisitor {

	public static void visit(final IdentifierNode ident, final String reg, final ListNode fun, final FileCompilerUnit fu) {
		fu.writeinstln("mov " + reg + ", " + fun.getSymbols().<LetDefSymbol>getSymbol(ident.getValue()).name());
	}

}

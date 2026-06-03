package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.ident.IdentifierNode;
import lu.pcy113.l3.parser.ast.let.LetSetNode;
import lu.pcy113.l3.parser.ast.symbols.LetDefSymbol;

public class LetSetVisitor {

	public static void visit(final LetSetNode letSet, final ListNode parent, final FileCompilerUnit fu) {
		fu.writeinstln("; LetSetVisitor");

		final LetDefSymbol letDefSymbol = parent.getSymbols()
				.<LetDefSymbol>getSymbol(((IdentifierNode) letSet.getParent()).getValue());

		VisitorHelper.compute(parent, letSet.getExpr(), "rax", fu);

		fu.writeinstln("mov " + letDefSymbol.name() + ", rax");
	}

}

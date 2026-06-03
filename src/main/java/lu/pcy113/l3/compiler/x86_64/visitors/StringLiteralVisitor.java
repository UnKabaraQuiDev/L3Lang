package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;

public class StringLiteralVisitor {

	static int currentStringIndex = 0;

	public static void visit(final StringLiteralNode node, final String reg, final ListNode parent, final FileCompilerUnit fu) {

		final String name = "str_" + StringLiteralVisitor.currentStringIndex++;

		fu.writedataln(name + " db \"" + node.getValue().getEscapedValue() + "\", 0");
		fu.writedataln(name + "_length equ $ - " + name);

		fu.writeinstln("mov " + reg + ", " + name);

	}

}

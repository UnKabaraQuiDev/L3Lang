package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.lit.StringLiteralNode;

public class StringLiteralVisitor {

	static int currentStringIndex = 0;

	public static void visit(StringLiteralNode node, String reg, ListNode parent, FileCompilerUnit fu) {

		final String name = "str_" + currentStringIndex++;

		fu.writedataln(name + " db \"" + node.getValue().getEscapedValue() + "\", 0");
		fu.writedataln(name + "_length equ $ - " + name);

		fu.writeinstln("mov " + reg + ", " + name);

	}

}

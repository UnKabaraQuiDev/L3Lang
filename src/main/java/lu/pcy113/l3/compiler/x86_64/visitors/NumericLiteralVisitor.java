package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;

public class NumericLiteralVisitor {

	public static void visit(final NumericLiteralNode num, final String reg, final FileCompilerUnit fu) {
		fu.writeinstln("; NumericLiteralVisitor");

		final Object val = num.getValue().getValue();

		if (val instanceof final Boolean bool) {
			fu.writeinstln("mov " + reg + ", " + (bool ? 1 : 0));
		} else if (val instanceof final Integer intVal) {
			fu.writeinstln("mov " + reg + ", " + intVal);
		} else {
			fu.implement(val);
		}
	}

}

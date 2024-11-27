package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.lit.NumericLiteralNode;

public class NumericLiteralVisitor {

	public static void visit(NumericLiteralNode num, String reg, FileCompilerUnit fu) {
		fu.writeinstln("; NumericLiteralVisitor");

		Object val = num.getValue().getValue();

		if (val instanceof Boolean bool) {
			fu.writeinstln("mov " + reg + ", " + (bool ? 1 : 0));
		} else if (val instanceof Integer intVal) {
			fu.writeinstln("mov " + reg + ", " + intVal);
		} else {
			fu.implement(val);
		}
	}

}

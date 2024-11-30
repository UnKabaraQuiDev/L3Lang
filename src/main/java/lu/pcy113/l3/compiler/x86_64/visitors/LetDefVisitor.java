package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.let.LetDefNode;
import lu.pcy113.l3.parser.ast.symbols.LetDefSymbol;
import lu.pcy113.l3.parser.ast.type.TypeNode;
import lu.pcy113.pclib.PCUtils;

public class LetDefVisitor {

	private static int currentStackOffset = 0;

	public static void visit(LetDefNode letDef, ListNode fun, FileCompilerUnit fu) {
		TypeNode type = letDef.getType();
		int byteCount = fixStackOffset(type.computeSize());

		final LetDefSymbol letDefSymbol = fun.getSymbols().get(letDef);

		if (letDef.isStatic()) {
			letDefSymbol.static_();

			String name = letDefSymbol.name();

			fu.writedataln(name + " resb " + byteCount);

			name = "[" + name + "]";
			letDefSymbol.setName(name);

			if (letDef.hasValue()) {
				VisitorHelper.compute(fun, letDef.getValue(), "rax", fu);
				fu.writeinstln("mov " + name + ", rax");
			}
		} else {
			currentStackOffset += byteCount;

			letDefSymbol.stack(currentStackOffset);

			if (letDef.hasValue()) {
				VisitorHelper.compute(fun, letDef.getValue(), "rax", fu);
				fu.writeinstln("push " + fu.getMemory().getAsSize("rax", byteCount), "Save (" + byteCount + "): " + letDef.getIdentifier().getValue());
			} else {
				fu.writeinstln("sub rsp, " + byteCount, "Save space (" + byteCount + ") for: " + letDef.getIdentifier().getValue());
			}
		}
	}

	private static int fixStackOffset(int byteSize) {
		return byteSize < 8 ? 8 : PCUtils.snap(byteSize, 8);
	}

}

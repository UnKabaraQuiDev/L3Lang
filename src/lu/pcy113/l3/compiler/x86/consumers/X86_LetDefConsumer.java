package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_LetDefConsumer extends CompilerConsumer<X86Compiler, LetDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, LetDefNode node) throws CompilerException {
		GlobalLogger.log("LetDef: " + node);

		int size = node.getType().getBytesSize();

		if (node.getExpr() instanceof NumLitNode) {

			if (!mem.hasFree()) {
				throw new CompilerException("No more free registers.");
			}

			String reg = mem.alloc();

			compiler.writeinstln("mov " + reg + ", " + ((NumLitNode) node.getExpr()).getValue());
			if (size >= 4) {
				node.getType().setBytesSize(8);
			} else if (size >= 1) {
				node.getType().setBytesSize(2);
			}
			size = node.getType().getBytesSize();
			compiler.writeinstln("push " + compiler.getMovType(size) + " " + mem.getAsSize(reg, size) + "  ; Alloc-ed: " + size + " for " + node.getIdent().asString());

			mem.free(reg);
		}
	}

}

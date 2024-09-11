package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.StructDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_StructDefConsumer extends CompilerConsumer<X86Compiler, StructDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, StructDefNode node) throws CompilerException {
		GlobalLogger.info("StructDef: " + node);

		int offset = 0;

		for (Node n : node) {
			if (n instanceof LetDefNode) {
				((LetDefNode) n).getType().normalizeSize(container);
				// compiler.writetextln(node.getLetDefDescriptor((LetDefNode) n).getAsmName() + " equ " + offset);
				offset += ((LetDefNode) n).getType().getBytesSize();
				node.getLetDefDescriptor((LetDefNode) n).setStackOffset(offset);
			}
		}

		System.out.println(node.toString(0));
	}

}

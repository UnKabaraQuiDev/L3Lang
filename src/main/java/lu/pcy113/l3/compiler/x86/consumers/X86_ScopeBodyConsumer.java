package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FunBodyDefNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_ScopeBodyConsumer extends CompilerConsumer<X86Compiler, ScopeBodyNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ScopeBodyNode node) throws CompilerException {
		GlobalLogger.log("ScopeBody: " + node);

		int stackOffset = 0;

		for (int i = 0; i < node.getChildren().size(); i++) {
			Node n = node.getChildren().get(i);

			compiler.compile(n);

			if (n instanceof LetDefNode) {
				stackOffset += ((LetDefNode) n).getType().getBytesSize();
			}
			
			mem.freeAll();
		}

		if (stackOffset != 0 && !(node instanceof FunBodyDefNode)) {
			compiler.writeinstln("add rsp, " + stackOffset);
		}
	}

}

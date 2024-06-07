package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_ScopeBodyConsumer extends CompilerConsumer<X86Compiler, ScopeBodyNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ScopeBodyNode node) throws CompilerException {
		GlobalLogger.log("ScopeBody: " + node);

		for (Node n : node) {
			compiler.compile(n);
		}
	}

}

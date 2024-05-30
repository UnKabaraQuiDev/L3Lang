package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class X86_RuntimeConsumer extends CompilerConsumer<X86Compiler, RuntimeNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, RuntimeNode node) throws CompilerException {
		compiler.compile(node.getMainFile());
	}
	
}

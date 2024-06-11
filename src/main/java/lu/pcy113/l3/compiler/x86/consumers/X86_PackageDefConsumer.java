package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.PackageDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class X86_PackageDefConsumer extends CompilerConsumer<X86Compiler, PackageDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, PackageDefNode node) {
		
	}
	
}

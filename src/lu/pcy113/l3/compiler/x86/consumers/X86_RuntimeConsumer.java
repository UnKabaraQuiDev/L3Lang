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
		compiler.writeinstln("mov rbp, rsp");

		compiler.writeinstln("sub rsp, " + node.getMainFile().getMainFunDescriptor().getNode().getReturnType().getBytesSize() + "  ; Freeing space for main-fun return");

		compiler.writeinstln("call " + node.getMainFile().getMainFunDescriptor().getAsmName() + "  ; Call main");
		// compiler.writeinstln("add rsp, "+node.getMainFile().getMainFunDescriptor().getNode().getMemorySize());

		compiler.writeinstln("; Final syscall exit");
		compiler.writeinstln("mov byte al, 60");
		compiler.writeinstln("mov byte dil, [rsp+8]");
		compiler.writeinstln("syscall");

		compiler.compile(node.getMainFile());
	}

}

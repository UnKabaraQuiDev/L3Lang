package lu.pcy113.l3.compiler.x86.consumers;

import java.util.stream.Collectors;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;

public class X86_RuntimeConsumer extends CompilerConsumer<X86Compiler, RuntimeNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, RuntimeNode node) throws CompilerException {
		compiler.writeln("_init:");
		for (LetScopeDescriptor desc : node.getMainFile().getDescriptors().values().stream().flatMap(c -> c.stream()).filter(c -> c instanceof LetScopeDescriptor).map(c -> (LetScopeDescriptor) c).collect(Collectors.toSet())) {
			compiler.compile(desc.getNode());
		}

		compiler.writeinstln("mov rbp, rsp");

		// compiler.writeinstln("sub rsp, " + node.getMainFile().getMainFunDescriptor().getNode().getReturnType().getBytesSize() + "  ; Freeing space for main-fun return");

		compiler.writeinstln("call main  ; Call main");

		compiler.writeinstln("; Final syscall exit");
		compiler.writeinstln("mov byte dil, al");
		compiler.writeinstln("mov qword rax, 60");
		compiler.writeinstln("syscall");

		compiler.compile(node.getMainFile());

		for (FileNode fn : node.getSecondaryFiles()) {
			compiler.compile(fn);
		}

	}

}

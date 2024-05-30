package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_ReturnConsumer extends CompilerConsumer<X86Compiler, ReturnNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ReturnNode node) throws CompilerException {
		GlobalLogger.log("Return: " + node);

		if (node.hasExpr()) {
			node.getFunDefParent().getReturnType().setBytesSize(8);
			int size = node.getFunDefParent().getReturnType().getBytesSize();
			compiler.writeinstln("mov " + compiler.getMovType(size) + " [rbp], " + mem.getAsSize("rax", size));
		}

		compiler.writeinstln("mov rsp, rbp");
		compiler.writeinstln("sub rsp, 16");

		compiler.writeinstln("ret");
	}

}

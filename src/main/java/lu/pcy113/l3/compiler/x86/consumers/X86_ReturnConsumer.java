package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ReturnNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_ReturnConsumer extends CompilerConsumer<X86Compiler, ReturnNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ReturnNode node) throws CompilerException {
		GlobalLogger.log("Return: " + node);

		if (node.hasExpr()) {
			if (node.getFunDefParent().getReturnType() instanceof PrimitiveTypeNode) {
				node.getFunDefParent().getReturnType().normalizeSize();
				int size = node.getFunDefParent().getReturnType().getBytesSize();
				// +24 bc rsp (call return), rbp and return rbp are on the stack

				compiler.writeinstln("; Return");

				compiler.compile(node.getExpr());
				String reg = mem.getLatest();

				if (!"rax".equals(reg)) {
					compiler.writeinstln("mov rax, " + reg);
				}

				// compiler.writeinstln("mov " + compiler.getMovType(size) + " [rbp+24], " + mem.getAsSize(reg, size));

				mem.free(reg);
			} else {
				compiler.implement();
			}
		}

		compiler.writeinstln("mov rsp, rbp");
		compiler.writeinstln("pop rbp");

		compiler.writeinstln("ret");
	}

}

package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
import lu.pcy113.l3.parser.ast.expr.RecursiveArithmeticOp;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.LetScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_LetDefConsumer extends CompilerConsumer<X86Compiler, LetDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, LetDefNode node) throws CompilerException {
		GlobalLogger.log("LetDef: " + node);

		LetScopeDescriptor letDesc = container.getLetDefDescriptor(node);

		node.getType().normalizeSize(container);
		int size = node.getType().getBytesSize();

		if (node.isiStatic()) { // global allocated
			if (node.getExpr() instanceof NumLitNode) {
				compiler.writedataln(letDesc.getAsmName() + " d" + compiler.getDataType(size) + " " + ((NumLitNode) node.getExpr()).getValue() + "  ; Defined: " + size + " for " + node.getIdent().asString());

				node.setAllocated(true);
			} else if (node.getExpr() instanceof RecursiveArithmeticOp) {
				compiler.writebssln(letDesc.getAsmName() + " resb " + size + "  ; Reserved: " + size + " for " + node.getIdent().asString());

				compiler.compile(node.getExpr());

				String reg = mem.getLatest();
				compiler.writeinstln("mov [" + letDesc.getAsmName() + "], " + reg);

				node.setAllocated(true);
			} else {
				compiler.implement();
			}
		} else { // stack allocated
			if (node.getExpr() instanceof RecursiveArithmeticOp) {
				compiler.compile(node.getExpr());

				letDesc.setStackOffset(mem.getCurrentStackOffset());
				mem.pushStack(node);

				String reg = mem.getLatest();

				compiler.writeinstln("push " + compiler.getMovType(size) + " " + mem.getAsSize("rax", size) + "  ; Alloc-ed: " + size + " for " + node.getIdent().asString());

				mem.free(reg);

				node.setAllocated(true);
			} else if (node.getExpr() instanceof UserTypeAllocNode) {
				final UserTypeAllocNode ua = (UserTypeAllocNode) node.getExpr();
				ua.getType().normalizeSize(container);
				size = ua.getType().getBytesSize();
				
				letDesc.setStackOffset(mem.getCurrentStackOffset());
				mem.pushStack(node);
				
				System.err.println(ua.toString(0));

				/*if(!mem.alloc("rbp")) {
					throw new CompilerException("Couldn't get lock on register: 'rbp'.");
				}*/
				
				mem.setLatest("rsp"); // Setting rbp as pointer for the location where the struct will be initialized

				compiler.compile(node.getExpr());
				
				compiler.writeinstln("sub rsp, " + size + "  ; Alloc-ed: " + size + " for " + node.getIdent().asString() + " type: " + ua.toString());

				node.setAllocated(true);
			} else {
				compiler.implement();
			}
		}

	}

}

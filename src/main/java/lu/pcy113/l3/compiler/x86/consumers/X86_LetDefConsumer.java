package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.UserTypeAllocNode;
<<<<<<< HEAD
=======
import lu.pcy113.l3.parser.ast.expr.ExplicitArrayDefNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
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
				compiler.writedataln(def.getAsmName() + " times " + size + " db " + " 0  ; Reserved empty: " + size + " for " + node.getIdent().asString());

				def.setAllocated(true);
			}
		} else { // stack allocated
			if (node.getExpr() instanceof RecursiveArithmeticOp) {
				compiler.compile(node.getExpr());

				letDesc.setStackOffset(mem.getCurrentStackOffset());
				mem.pushStack(node);

				String reg = mem.getLatest();

				compiler.writeinstln("push " + compiler.getMovType(size) + " " + mem.getAsSize("rax", size) + "  ; Alloc-ed: " + size + " for " + node.getIdent().asString());

				mem.free(reg);

<<<<<<< HEAD
				def.setAllocated(true);
			} else if (node.getExpr() instanceof UserTypeAllocNode) {
				// TODO add support for other than struct

				mem.pushStack(node);

				// compiler.writeinstln("sub rsp, " + size + " ; Alloc-ed empty: " + size + " for " + node.getIdent().asString());

				UserTypeAllocNode ua = (UserTypeAllocNode) node.getExpr();

				compiler.compile(ua);

				def.setAllocated(true);
=======
				node.setAllocated(true);
			} else if (node.getExpr() instanceof UserTypeAllocNode) {
				final UserTypeAllocNode ua = (UserTypeAllocNode) node.getExpr();
				ua.getType().normalizeSize(container);

				letDesc.setStackOffset(mem.getCurrentStackOffset());
				mem.pushStack(node);

				compiler.compile(ua);

				node.setAllocated(true);
			} else if (node.getExpr() instanceof ExplicitArrayDefNode) {
				letDesc.setStackOffset(mem.getCurrentStackOffset());
				mem.pushStack(node);
				
				ExplicitArrayDefNode arrayDef = (ExplicitArrayDefNode) node.getExpr();
				
				compiler.compile(arrayDef);
				
				node.setAllocated(true);
>>>>>>> branch 'main' of git@github.com:UnKabaraQuiDev/L3Lang.git
			} else {
				mem.pushStack(node);

				compiler.writeinstln("sub rsp, " + size + "  ; Alloc-ed empty: " + size + " for " + node.getIdent().asString());

				def.setAllocated(true);
			}
		}

	}

}

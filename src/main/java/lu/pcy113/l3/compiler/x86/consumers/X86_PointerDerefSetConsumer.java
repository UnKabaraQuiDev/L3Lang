package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.MemoryUtil;
import lu.pcy113.l3.parser.ast.PointerDerefSetNode;
import lu.pcy113.l3.parser.ast.expr.PointerDerefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_PointerDerefSetConsumer extends CompilerConsumer<X86Compiler, PointerDerefSetNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, PointerDerefSetNode node) throws CompilerException {
		GlobalLogger.log("PointerDerefSet: " + node);

		PointerDerefNode pointer = node.getPointer();

		compiler.compile(pointer.getExpr());
		String pointerLoc = mem.getLatest();
		
		compiler.compile(node.getExpr());
		String exprLoc = mem.getLatest();

		compiler.writeinstln("mov [" + pointerLoc + "], " + mem.getAsSize(exprLoc, MemoryUtil.getPrimitiveSize(MemoryUtil.POINTER_TYPE)) + "  ; Save var to pointer");

		mem.free(exprLoc);
		mem.free(pointerLoc);
	}

}

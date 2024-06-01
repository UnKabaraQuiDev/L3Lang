package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.FunScopeDescriptor;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.pclib.GlobalLogger;

public class X86_FunCallConsumer extends CompilerConsumer<X86Compiler, FunCallNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FunCallNode node) throws CompilerException {
		GlobalLogger.log("FunCall: " + node);

		FunScopeDescriptor def = container.getFunDefDescriptor(node);
		FunDefNode funDef = def.getNode();

		funDef.getReturnType().normalizeSize();
		int returnSize = funDef.getReturnType().getBytesSize();

		funDef.getParams().normalizeSize();
		int paramsSize = funDef.getParams().getBytesSize();

		if (!funDef.getParams().paramsEquals(node.getParams())) {
			throw new CompilerException("Method parameters do not match.");
		}

		compiler.writeinstln("; Preparing call: " + funDef.getIdent().getLeaf().getValue());

		if (!(funDef.getReturnType() instanceof PrimitiveTypeNode)) {
			compiler.writeinstln("sub rsp, " + returnSize + "  ; Freeing space for FunCall return");
		}

		for (int i = 0; i < node.getParams().getChildren().size(); i++) {
			Node n = node.getParams().getParam(i);

			compiler.compile(n);
			int paramSize = funDef.getParams().getParam(i).getType().getBytesSize();
			compiler.writeinstln("push " + compiler.getMovType(paramSize) + " " + mem.getAsSize(mem.getLatest(), paramSize));
			mem.free(mem.getLatest());
			// mem.pushStack(n);
		}

		if (!mem.isFree("rax")) {
			throw new CompilerException("RAX is not free for fun return.");
		}

		mem.alloc("rax");

		compiler.writeinstln("call " + def.getAsmName() + "  ; Call: " + funDef.getIdent().getLeaf().getValue());

		/*
		 * for (Node n : node.getParams()) { mem.popStack(); }
		 */

		if (paramsSize != 0) {
			compiler.writeinstln("add rsp, " + paramsSize + "  ; Freeing mem from arguments");
		}

		mem.setLatest("rax");
	}

}

package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.FunDefParamNode;
import lu.pcy113.l3.parser.ast.scope.FunDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_FunDefConsumer extends CompilerConsumer<X86Compiler, FunDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, FunDefNode node) throws CompilerException {
		GlobalLogger.log("FunDef: " + node);

		mem.freeAll();

		if(!node.isReturnSafe()) {
			throw new CompilerException("FunDef is not return-safe !");
		}
		
		node.getParams().normalizeSize();
		int offset = 0; // node.getParams().getBytesSize();
		for (int i = 0; i < node.getParams().getChildren().size(); i++) {
			FunDefParamNode param = node.getParams().getParam(i);
			node.getParamDefDescriptor(param).setStackOffset(offset);
			offset += param.getType().getBytesSize();
		}

		compiler.writeln(node.getFunDefDescriptor(node).getAsmName() + ":  ; Fun: " + node.getIdent().asString());
		compiler.writeinstln("push rbp");
		compiler.writeinstln("mov rbp, rsp");

		compiler.writeinstln(";  Fun body start - - -");
		
		compiler.compile(node.getBody());
		
		compiler.writeinstln(";  Fun body end - - -");

		mem.clearStack();
	}

}

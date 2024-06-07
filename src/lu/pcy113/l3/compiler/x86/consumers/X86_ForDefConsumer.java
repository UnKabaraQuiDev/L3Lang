package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ForDefNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_ForDefConsumer extends CompilerConsumer<X86Compiler, ForDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, ForDefNode node) throws CompilerException {
		GlobalLogger.log("ForDef: " + node);

		compiler.writeln("; For start - - -");

		node.setAsmName("." + compiler.newSection());
		compiler.writeln(node.getAsmName() + ":  ; " + node.getAsmName());

		ScopeBodyNode body = node.getBody();

		body.setAsmName(node.getAsmName() + "_body");

		if (node.hasLet()) {
			compiler.writeinstln("; Let:");
			compiler.compile(node.getLet());
		}

		compiler.writeln(body.getAsmName() + ":");

		if (node.hasCondition()) {
			compiler.writeinstln("; Condition:");
			compiler.compile(node.getCondition());

			compiler.writeinstln("jz " + node.getAsmName() + "_end");
		}

		compiler.writeinstln("; Body:");

		compiler.compile(body);

		if (node.hasInc()) {
			compiler.writeinstln("; Inc:");
			compiler.compile(node.getInc());
		}

		compiler.writeinstln("jmp " + body.getAsmName());

		compiler.writeln(node.getAsmName() + "_end:");

		if (node.hasLet()) {
			compiler.writeinstln("add rsp, " + ((LetDefNode) node.getLet()).getType().getBytesSize());
		}

		compiler.writeln("; For end - - -");

	}

}

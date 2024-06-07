package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.WhileDefNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_WhileDefConsumer extends CompilerConsumer<X86Compiler, WhileDefNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, WhileDefNode node) throws CompilerException {
		GlobalLogger.log("WhileDef: " + node);

		compiler.writeln("; While start - - -");

		node.setAsmName("." + compiler.newSection());
		compiler.writeln(node.getAsmName() + ":  ; " + node.getAsmName());

		ScopeBodyNode body = node.getBody();

		body.setAsmName(node.getAsmName() + "_body");

		compiler.writeln(body.getAsmName() + ":");

		if (node.hasCondition()) {
			compiler.writeinstln("; Condition:");
			compiler.compile(node.getCondition());

			compiler.writeinstln("jz " + node.getAsmName() + "_end");
		}

		compiler.writeinstln("; Body:");

		compiler.compile(body);

		compiler.writeinstln("jmp " + body.getAsmName());

		compiler.writeln(node.getAsmName() + "_end:");

		compiler.writeln("; While end - - -");

	}

}

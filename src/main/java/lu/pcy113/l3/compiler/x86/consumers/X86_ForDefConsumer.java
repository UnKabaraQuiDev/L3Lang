package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.ForDefNode;
import lu.pcy113.l3.parser.ast.LetDefNode;
import lu.pcy113.l3.parser.ast.ScopeBodyNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

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

		if (node.hasCondition()) {
			compiler.writeinstln("; First condition:");
			compiler.compile(node.getCondition());

			mem.free(mem.getLatest());

			compiler.writeinstln("cmp " + mem.getLatest() + ", 0");
			compiler.writeinstln("jz " + node.getAsmName() + "_else");
		}

		compiler.writeln(body.getAsmName() + ":");
		compiler.writeinstln("; Body:");

		compiler.compile(body);

		if (node.hasInc()) {
			compiler.writeinstln("; Inc:");
			compiler.compile(node.getInc());
		}

		if (node.hasCondition()) {
			compiler.writeinstln("; In-body condition:");
			compiler.compile(node.getCondition());

			mem.free(mem.getLatest());

			compiler.writeinstln("cmp " + mem.getLatest() + ", 0");
			compiler.writeinstln("jz " + node.getAsmName() + "_final");
		}

		compiler.writeinstln("jmp " + body.getAsmName());

		compiler.writeln(node.getAsmName() + "_final:");

		if (node.hasFinally()) {
			compiler.compile(node.getFinally().getBody());
		}
		compiler.writeinstln("jmp " + node.getAsmName() + "_end");

		compiler.writeln(node.getAsmName() + "_else:");

		if (node.hasElse()) {
			compiler.compile(node.getElse().getBody());
		}

		compiler.writeln(node.getAsmName() + "_end:");

		if (node.hasLet()) {
			compiler.writeinstln("add rsp, " + ((LetDefNode) node.getLet()).getType().getBytesSize());
		}

		compiler.writeln("; For end - - -");

	}

}

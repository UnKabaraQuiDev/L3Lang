package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.parser.ast.AsmNamed;
import lu.pcy113.l3.parser.ast.ElseDefNode;
import lu.pcy113.l3.parser.ast.FinallyDefNode;
import lu.pcy113.l3.parser.ast.IfContainerNode;
import lu.pcy113.l3.parser.ast.IfDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_IfContainerConsumer extends CompilerConsumer<X86Compiler, IfContainerNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, IfContainerNode node) throws CompilerException {
		GlobalLogger.log("IfContainer: " + node);

		compiler.writeln("; If container start - - -");

		node.setAsmName("." + compiler.newSection());
		compiler.writeln(node.getAsmName() + ":  ; " + node.getAsmName());

		for (int i = 0; i < node.getChildren().size(); i++) {
			Node sub = node.getChildren().get(i);

			if (sub instanceof FinallyDefNode) {
				((FinallyDefNode) sub).setAsmName(node.getAsmName() + "_final");
			} else {
				((AsmNamed) sub).setAsmName(node.getAsmName() + "_" + i);
			}

			if (sub instanceof IfDefNode) {
				compiler.compile(((IfDefNode) sub).getCondition());
				String reg = mem.getLatest();
				compiler.writeinstln("cmp " + reg + ", 0");
				compiler.writeinstln("jnz " + ((IfDefNode) sub).getAsmName() + "  ; If: " + ((IfDefNode) sub).getCondition());
			} else if (sub instanceof FinallyDefNode) {
				// pass
			} else if (sub instanceof ElseDefNode) {
				compiler.writeinstln("jmp " + ((ElseDefNode) sub).getAsmName() + "  ; Else");
			}
		}

		compiler.writeinstln("jmp " + node.getAsmName() + "_end");

		for (int i = 0; i < node.getChildren().size(); i++) {
			Node sub = node.getChildren().get(i);

			compiler.writeln(((AsmNamed) sub).getAsmName() + ":");

			if (sub instanceof IfDefNode) {
				compiler.compile(((IfDefNode) sub).getBody());
				if (node.hasFinally()) {
					compiler.writeinstln("jmp " + node.getFinally().getAsmName());
				} else {
					compiler.writeinstln("jmp " + node.getAsmName() + "_end");
				}
			} else if (sub instanceof FinallyDefNode) {
				compiler.compile(((FinallyDefNode) sub).getBody());
				compiler.writeinstln("jmp " + node.getAsmName() + "_end");
			} else if (sub instanceof ElseDefNode) {
				compiler.compile(((ElseDefNode) sub).getBody());
			} else {
				compiler.implement(sub);
			}
		}

		compiler.writeln(node.getAsmName() + "_end:");
		compiler.writeln("; If container end - - -");

	}

}

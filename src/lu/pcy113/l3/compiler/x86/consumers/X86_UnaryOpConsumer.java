package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.FieldAccessNode;
import lu.pcy113.l3.parser.ast.LetSetNode;
import lu.pcy113.l3.parser.ast.RegisterValueNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.expr.UnaryOpNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_UnaryOpConsumer extends CompilerConsumer<X86Compiler, UnaryOpNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, UnaryOpNode node) throws CompilerException {
		GlobalLogger.log("UnaryOp: " + node);

		TokenType type = node.getOperator();
		ExprNode expr = node.getExpr();

		compiler.compile(expr);
		String reg = mem.getLatest();
		
		if (node.isPrefix()) {
			switch (type) {
			case NOT:
			case BIT_NOT:
				compiler.writeinstln("not " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				if (type.equals(TokenType.NOT)) {
					compiler.writeinstln("cmp " + reg + ", 0");
					compiler.writeinstln("setg " + mem.getAsSize(reg, 1));
					compiler.writeinstln("movzx " + reg + ", " + mem.getAsSize(reg, 1));
				}
				break;
			default:
				throw new CompilerException("Prefix operation not supported: " + type);
			}
		} else if (node.isPostfix()) {
			switch (type) {
			case PLUS_PLUS:
				compiler.writeinstln("inc " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				break;
			case MINUS_MINUS:
				compiler.writeinstln("dec " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				break;
			default:
				throw new CompilerException("Postfix operation not supported: " + type);
			}
		}

		if(expr instanceof FieldAccessNode) {
			LetSetNode artificialNode = new LetSetNode((FieldAccessNode) expr, new RegisterValueNode(reg, expr.isDecimal(), expr.isInteger()));
			node.getParent().add(artificialNode);
			compiler.compile(artificialNode);
			node.getParent().remove(artificialNode);
		}
		
	}

}

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

		if (!(expr instanceof FieldAccessNode)) {
			throw new CompilerException("What do you think you're doing ?");
		}
		
		FieldAccessNode fieldAccess = (FieldAccessNode) expr;
		
		compiler.compile(fieldAccess);
		String reg = mem.getLatest();

		// add prefix for PLUS_PLUS and MINUS_MINUS, save old value in reg, then return reg with mem.setLatest()

		String oldReg = mem.alloc();

		if (node.isPrefix()) {
			switch (type) {
			case NOT:
				compiler.writeinstln("not " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				compiler.writeinstln("cmp " + reg + ", 0");
				compiler.writeinstln("setg " + mem.getAsSize(reg, 1));
				compiler.writeinstln("movzx " + reg + ", " + mem.getAsSize(reg, 1) + "  ; UnaryNode: normalized logical 'not'");
				break;
			case BIT_NOT:
				compiler.writeinstln("not " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				break;
			case PLUS_PLUS:
				compiler.writeinstln("mov " + oldReg + ", " + reg+"  ; UnaryNode: save value bc prefix operation");
				compiler.writeinstln("inc " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
				break;
			case MINUS_MINUS:
				compiler.writeinstln("mov " + oldReg + ", " + reg+"  ; UnaryNode: save value bc prefix operation");
				compiler.writeinstln("dec " + reg + "  ; UnaryNode: " + type.name() + " " + node.toString());
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
		} else {
			throw new CompilerException("Unary operation not supported: " + type);
		}

		LetSetNode artificialNode = new LetSetNode(fieldAccess, new RegisterValueNode(reg, fieldAccess.isDecimal(), fieldAccess.isInteger()));
		node.getParent().add(artificialNode);
		compiler.compile(artificialNode);
		node.getParent().remove(artificialNode);
		
		if(node.isPrefix() && (type.equals(TokenType.PLUS_PLUS) || type.equals(TokenType.MINUS_MINUS))) {
			mem.free(reg);
			mem.setLatest(oldReg);
		}

	}

}

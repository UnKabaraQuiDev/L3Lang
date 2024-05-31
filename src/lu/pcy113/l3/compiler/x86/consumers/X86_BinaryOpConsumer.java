package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.lit.NumLitNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.GlobalLogger;

public class X86_BinaryOpConsumer extends CompilerConsumer<X86Compiler, BinaryOpNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, BinaryOpNode node) throws CompilerException {
		GlobalLogger.log("NumLit: " + node);

		ExprNode left = (ExprNode) ((BinaryOpNode) node).getLeft();
		ExprNode right = (ExprNode) ((BinaryOpNode) node).getRight();
		TokenType operator = ((BinaryOpNode) node).getOperator();

		if (left instanceof NumLitNode) {
			compiler.compile(left);
		} else {
			compiler.compile(left);
		}
		String regLeft = mem.getLatest();

		if (right instanceof NumLitNode) {
			compiler.compile(right);
		} else {
			compiler.compile(right);
		}
		String regRight = mem.getLatest();

		switch (operator) {
		case OR:
		case PLUS:
			compiler.writeinstln("add " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case MINUS:
			compiler.writeinstln("sub " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case MODULO:
		case DIV:
			if (regLeft != "rax") {
				String regOther = mem.alloc();
				compiler.writeinstln("mov " + regOther + ", " + regLeft);
				compiler.writeinstln("mov " + regLeft + ", " + regRight);
				compiler.writeinstln("mov " + regRight + ", " + regOther);
				mem.free(regOther);
				regOther = regLeft;
				regLeft = regRight;
				regRight = regOther;
			}
			compiler.writeinstln("mov rdx, 0");
			compiler.writeinstln("idiv " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			if (TokenType.MODULO.equals(operator)) {
				compiler.writeinstln("mov " + regLeft + ", rdx");
			} else if (TokenType.DIV.equals(operator)) {
				compiler.writeinstln("mov " + regLeft + ", rax");
			}
			break;
		case AND:
		case MUL:
			compiler.writeinstln("imul " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case XOR:
			compiler.writeinstln("xor " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case EQUALS:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("sete " + mem.getAsSize(regLeft, 1));
			break;
		case NOT_EQUALS:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setne " + mem.getAsSize(regLeft, 1));
			break;
		case LESS:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setl " + mem.getAsSize(regLeft, 1));
			break;
		case LESS_EQUALS:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setle " + mem.getAsSize(regLeft, 1));
			break;
		case GREATER:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setg " + mem.getAsSize(regLeft, 1));
			break;
		case GREATER_EQUALS:
			compiler.writeinstln("cmp " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setge " + mem.getAsSize(regLeft, 1));
			break;
		default:
			throw new CompilerException("Operation not supported: " + operator);
		}

		mem.free(regRight);

		if (TokenType.OR.equals(operator) || TokenType.AND.equals(operator) || TokenType.XOR.equals(operator)) {
			compiler.writeinstln("cmp " + regLeft + ", 0");
			compiler.writeinstln("setg " + mem.getAsSize(regLeft, 1));
		}

		mem.setLatest(regLeft);

	}

}

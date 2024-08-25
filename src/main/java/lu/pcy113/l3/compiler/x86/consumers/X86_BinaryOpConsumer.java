package lu.pcy113.l3.compiler.x86.consumers;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.consumers.CompilerConsumer;
import lu.pcy113.l3.compiler.memory.MemoryStatus;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.expr.BinaryOpNode;
import lu.pcy113.l3.parser.ast.expr.ExprNode;
import lu.pcy113.l3.parser.ast.scope.ScopeContainer;
import lu.pcy113.pclib.logger.GlobalLogger;

public class X86_BinaryOpConsumer extends CompilerConsumer<X86Compiler, BinaryOpNode> {

	@Override
	protected void accept(X86Compiler compiler, MemoryStatus mem, ScopeContainer container, BinaryOpNode node) throws CompilerException {
		GlobalLogger.log("BinOp: " + node);

		ExprNode left = (ExprNode) ((BinaryOpNode) node).getLeft();
		ExprNode right = (ExprNode) ((BinaryOpNode) node).getRight();
		TokenType operator = ((BinaryOpNode) node).getOperator();

		compiler.compile(left);
		String regLeft = mem.getLatest();
		compiler.writeinstln("push " + regLeft);
		mem.free(regLeft);

		compiler.compile(right);
		String regRight = mem.getLatest();

		regLeft = mem.alloc();
		compiler.writeinstln("pop " + regLeft);

		if (node.isInteger()) {
			integer(regLeft, regRight, left, operator, right, mem, compiler);
		} else if (node.isFloat() || node.isDouble()) {
			float_double(node, regLeft, regRight, left, operator, right, mem, compiler);
		} else {
			compiler.implement();
		}

	}

	private void float_double(BinaryOpNode node, String regLeft, String regRight, ExprNode left, TokenType operator, ExprNode right, MemoryStatus mem, X86Compiler compiler) throws CompilerException {
		final String regLeftFP = mem.allocFP();
		final String regRightFP = mem.allocFP();

		String opCodeSuffix = node.isFloat() ? "ss" : (node.isDouble() ? "sd" : null);

		if (node.isFloat()) {
			if (left.isFloat()) {
				compiler.writeinstln("movd " + regLeftFP + ", " + mem.getAsSize(regLeft, 4));
			} else if (left.isInteger()) {
				compiler.writeinstln("cvtsi2ss " + regLeftFP + ", " + mem.getAsSize(regLeft, 8) + "  ; Convert int to float");
			}

			if (right.isFloat()) {
				compiler.writeinstln("movd " + regRightFP + ", " + mem.getAsSize(regRight, 4));
			} else if (right.isInteger()) {
				compiler.writeinstln("cvtsi2ss " + regRightFP + ", " + mem.getAsSize(regRight, 8) + "  ; Convert int to float");
			}
		} else if (node.isDouble()) {
			if (left.isFloat()) {
				compiler.writeinstln("movd " + regLeftFP + ", " + mem.getAsSize(regLeft, 4));
				compiler.writeinstln("cvtss2sd " + regLeftFP + ", " + regLeftFP + "  ; Convert float to double");
			} else if (left.isDouble()) {
				compiler.writeinstln("movq " + regLeftFP + ", " + mem.getAsSize(regLeft, 8));
			} else if (left.isInteger()) {
				compiler.writeinstln("cvtsi2sd " + regLeftFP + ", " + mem.getAsSize(regLeft, 8) + "  ; Convert int to double");
			}

			if (right.isFloat()) {
				compiler.writeinstln("movd " + regRightFP + ", " + mem.getAsSize(regRight, 4));
				compiler.writeinstln("cvtss2sd " + regRightFP + ", " + regRightFP + "  ; Convert float to double");
			} else if (right.isDouble()) {
				compiler.writeinstln("movq " + regRightFP + ", " + mem.getAsSize(regRight, 8));
			} else if (right.isInteger()) {
				compiler.writeinstln("cvtsi2sd " + regRightFP + ", " + mem.getAsSize(regRight, 8) + "  ; Convert int to double");
			}
		}

		mem.free(regRight);
		mem.free(regLeft);

		regLeft = regLeftFP;
		regRight = regRightFP;

		switch (operator) {
		case OR:
		case PLUS:
			compiler.writeinstln("add" + opCodeSuffix + " " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case MINUS:
			compiler.writeinstln("sub" + opCodeSuffix + " " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case MODULO:
			compiler.implement();
			break;
		case DIV:
			compiler.writeinstln("div" + opCodeSuffix + " " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case AND:
		case MUL:
			compiler.writeinstln("mul" + opCodeSuffix + " " + regLeft + ", " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			break;
		case EQUALS:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("sete " + mem.getAsSize(regLeft, 1));
			break;
		case NOT_EQUALS:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setne " + mem.getAsSize(regLeft, 1));
			break;
		case LESS:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setl " + mem.getAsSize(regLeft, 1));
			break;
		case LESS_EQUALS:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setle " + mem.getAsSize(regLeft, 1));
			break;
		case GREATER:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setg " + mem.getAsSize(regLeft, 1));
			break;
		case GREATER_EQUALS:
			compiler.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			compiler.writeinstln("setge " + mem.getAsSize(regLeft, 1));
			break;
		default:
			throw new CompilerException("Operation not supported: " + operator);
		}

		mem.free(regRight);

		mem.setLatest(regLeft);
	}

	private void integer(String regLeft, String regRight, ExprNode left, TokenType operator, ExprNode right, MemoryStatus mem, X86Compiler compiler) throws CompilerException {
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
			if (!regLeft.equals("rax") && !mem.isFree("rax")) {
				String regOther = mem.alloc();
				compiler.writeinstln(";  Swapping regs: " + regLeft + " & " + regRight + " through " + regOther);
				compiler.writeinstln("mov " + regOther + ", " + regLeft);
				compiler.writeinstln("mov " + regLeft + ", " + regRight);
				compiler.writeinstln("mov " + regRight + ", " + regOther);
				mem.free(regOther);
				regOther = regLeft;
				regLeft = regRight;
				regRight = regOther;
			} else if (!regLeft.equals("rax") && mem.isFree("rax")) {
				compiler.writeinstln("mov qword rax, " + regLeft);
			}
			// compiler.writeinstln("mov qword rax, "+regLeft);
			compiler.writeinstln("mov qword rdx, 0");
			compiler.writeinstln("idiv " + regRight + "  ; " + left + " " + operator.name() + " " + right + " -> " + regLeft);
			if (TokenType.MODULO.equals(operator)) {
				compiler.writeinstln("mov " + regLeft + ", rdx  ; Bc its mod");
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

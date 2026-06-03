package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.L3Exception;
import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.abstr.ImplicitType;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.math.BinaryExpressionNode;

public class BinaryExpressionVisitor {

	public static void visit(final BinaryExpressionNode bin, final String reg, final ListNode parent, final FileCompilerUnit fu) {

		final Node left = bin.getLeft();
		final Node right = bin.getRight();

		final String regLeft = "rax", regRight = "rbx";

		VisitorHelper.compute(parent, left, "rax", fu);
		fu.writeinstln("push rax");

		VisitorHelper.compute(parent, right, regRight, fu);

		fu.writeinstln("pop " + regLeft);

		final ImplicitType implicitType = ImplicitType.computeType(bin, parent);

		String retReg = null;
		if (implicitType.isInt()) {
			retReg = BinaryExpressionVisitor.integer(bin, parent, regLeft, regRight, fu);
		} else if (implicitType.isFloat() || implicitType.isDouble()) {
			retReg = BinaryExpressionVisitor.float_double(bin, parent, regLeft, regRight, fu);
		} else {
			fu.implement();
		}

		if (!reg.equals(retReg)) {
			fu.writeinstln("mov " + reg + ", " + retReg);
		}
	}

	private static String float_double(final BinaryExpressionNode node, final ListNode parent, String regLeft, String regRight,
			final FileCompilerUnit fu) {
		final Node left = node.getLeft(), right = node.getRight();

		//@formatter:off
		final boolean nodeFloat = ImplicitType.computeType(node, parent).isFloat(),
				leftFloat = ImplicitType.computeType(left, parent).isFloat(),
				rightFloat = ImplicitType.computeType(right, parent).isFloat(),
				nodeInt = ImplicitType.computeType(node, parent).isInt(),
				leftInt = ImplicitType.computeType(left, parent).isInt(),
				rightInt = ImplicitType.computeType(right, parent).isInt(),
				nodeDouble = ImplicitType.computeType(node, parent).isDouble(),
				leftDouble = ImplicitType.computeType(left, parent).isDouble(),
				rightDouble = ImplicitType.computeType(right, parent).isDouble();
		//@formatter:on

		final String opCodeSuffix = nodeFloat ? "ss" : nodeDouble ? "sd" : null;

		final String regLeftFP = "xmm0", regRightFP = "xmm1";

		final TokenType operator = node.getOperator();

		fu.writeinstln("; float_double");

		if (nodeFloat) {
			if (leftFloat) {
				fu.writeinstln("movd " + regLeftFP + ", " + regLeft);
			} else if (leftInt) {
				fu.writeinstln("cvtsi2ss " + regLeftFP + ", " + regLeft + "  ; Convert int to float");
			}

			if (rightFloat) {
				fu.writeinstln("movd " + regRightFP + ", " + regRight);
			} else if (rightInt) {
				fu.writeinstln("cvtsi2ss " + regRightFP + ", " + regRight + "  ; Convert int to float");
			}
		} else if (nodeDouble) {
			if (leftFloat) {
				fu.writeinstln("movd " + regLeftFP + ", " + regLeft);
				fu.writeinstln("cvtss2sd " + regLeftFP + ", " + regLeftFP + "  ; Convert float to double");
			} else if (leftDouble) {
				fu.writeinstln("movq " + regLeftFP + ", " + regLeft);
			} else if (leftInt) {
				fu.writeinstln("cvtsi2sd " + regLeftFP + ", " + regLeft + "  ; Convert int to double");
			}

			if (rightFloat) {
				fu.writeinstln("movd " + regRightFP + ", " + regRight);
				fu.writeinstln("cvtss2sd " + regRightFP + ", " + regRightFP + "  ; Convert float to double");
			} else if (rightDouble) {
				fu.writeinstln("movq " + regRightFP + ", " + regRight);
			} else if (rightInt) {
				fu.writeinstln("cvtsi2sd " + regRightFP + ", " + regRight + "  ; Convert int to double");
			}
		}

		regLeft = regLeftFP;
		regRight = regRightFP;

		switch (operator) {
		case OR, PLUS -> fu.writeinstln("add" + opCodeSuffix + " " + regLeft + ", " + regRight);
		case MINUS -> fu.writeinstln("sub" + opCodeSuffix + " " + regLeft + ", " + regRight);
		case MODULO -> fu.implement();
		case DIV -> fu.writeinstln("div" + opCodeSuffix + " " + regRight);
		case AND, MUL -> fu.writeinstln("mul" + opCodeSuffix + " " + regLeft + ", " + regRight);
		case EQUALS -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("sete " + regLeft);
		}
		case NOT_EQUALS -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("setne " + regLeft);
		}
		case LESS -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("setl " + regLeft);
		}
		case LESS_EQUALS -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("setle " + regLeft);
		}
		case GREATER -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("setg " + regLeft);
		}
		case GREATER_EQUALS -> {
			fu.writeinstln("ucomi" + opCodeSuffix + " " + regLeft + ", " + regRight + "");
			fu.writeinstln("setge " + regLeft);
		}
		default -> throw new L3Exception("Operation not supported: " + operator);
		}

		return regLeft;
	}

	private static String integer(final BinaryExpressionNode node, final ListNode parent, final String regLeft, final String regRight,
			final FileCompilerUnit fu) {
		final Node left = node.getLeft(), right = node.getRight();

		//@formatter:off
		final boolean nodeFloat = ImplicitType.computeType(node, parent).isFloat(),
				leftFloat = ImplicitType.computeType(left, parent).isFloat(),
				rightFloat = ImplicitType.computeType(right, parent).isFloat(),
				nodeInt = ImplicitType.computeType(node, parent).isInt(),
				leftInt = ImplicitType.computeType(left, parent).isInt(),
				rightInt = ImplicitType.computeType(right, parent).isInt(),
				nodeDouble = ImplicitType.computeType(node, parent).isDouble(),
				leftDouble = ImplicitType.computeType(left, parent).isDouble(),
				rightDouble = ImplicitType.computeType(right, parent).isDouble();
		//@formatter:on

		final TokenType operator = node.getOperator();

		fu.writeinstln("; integer");

		switch (operator) {
		case OR:
		case PLUS:
			fu.writeinstln("add " + regLeft + ", " + regRight);
			break;
		case MINUS:
			fu.writeinstln("sub " + regLeft + ", " + regRight);
			break;
		case MODULO:
		case DIV:
			if (!"rax".equals(regLeft)) {
				throw new L3Exception("Left reg should be rax !");
			}
			fu.writeinstln("mov qword rdx, 0");
			fu.writeinstln("idiv " + regRight);
			if (TokenType.MODULO.equals(operator)) {
				fu.writeinstln("mov " + regLeft + ", rdx  ; Bc its mod");
			} else if (TokenType.DIV.equals(operator)) {
				fu.writeinstln("mov " + regLeft + ", rax");
			}
			break;
		case AND:
		case MUL:
			fu.writeinstln("imul " + regLeft + ", " + regRight);
			break;
		case XOR:
			fu.writeinstln("xor " + regLeft + ", " + regRight);
			break;
		case EQUALS:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("sete " + regLeft);
			break;
		case NOT_EQUALS:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("setne " + regLeft);
			break;
		case LESS:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("setl " + regLeft);
			break;
		case LESS_EQUALS:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("setle " + regLeft);
			break;
		case GREATER:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("setg " + regLeft);
			break;
		case GREATER_EQUALS:
			fu.writeinstln("cmp " + regLeft + ", " + regRight + "");
			fu.writeinstln("setge " + regLeft);
			break;
		default:
			throw new L3Exception("Operation not supported: " + operator);
		}

		if (TokenType.OR.equals(operator) || TokenType.AND.equals(operator) || TokenType.XOR.equals(operator)) {
			fu.writeinstln("cmp " + regLeft + ", 0");
			fu.writeinstln("setg " + regLeft);
		}

		return regLeft;
	}

}

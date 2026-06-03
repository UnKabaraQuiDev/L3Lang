package lu.pcy113.l3.compiler.x86_64.visitors;

import lu.pcy113.l3.compiler.L3Compiler.FileCompilerUnit;
import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.lexer.tokens.NumericLiteralToken.NumericValueType;
import lu.pcy113.l3.parser.ast.abstr.ListNode;
import lu.pcy113.l3.parser.ast.abstr.Node;
import lu.pcy113.l3.parser.ast.type.CastNode;
import lu.pcy113.l3.parser.ast.type.PrimitiveTypeNode;
import lu.pcy113.l3.parser.ast.type.TypeNode;

public class CastVisitor {

	public static void visit(final CastNode cast, final String reg, final ListNode parent, final FileCompilerUnit fu) {
		final TypeNode castType = cast.getCastType();
		final Node value = cast.getValue();

		if (castType instanceof final PrimitiveTypeNode primitive) {
			VisitorHelper.compute(parent, value, "rax", fu);

			final NumericValueType primType = NumericValueType.byTokenType(primitive.getType());

			if (TokenType.BOOLEAN.equals(primitive.getType())) {
				fu.writeinstln("cmp rax, 0", "Cast to boolean");
				fu.writeinstln("setg al");
				fu.writeinstln("movzx rax, al");
			} else {
				final String sizedReg = fu.getMemory().getAsSize("rax", primType.getBytes());

				if (primType.isSigned()) {
					fu.writeinstln("movsx rax, " + sizedReg, "Cast to: " + primitive.getType());
				} else if (primType.isUnsigned()) {
					fu.writeinstln("movzx rax, " + sizedReg, "Cast to: " + primitive.getType());
				}
			}
		}

	}

}

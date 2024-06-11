package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.scope.ScopeContainerNode;
import lu.pcy113.l3.utils.CompilerOptions;

public class ScopeBodyNode extends ScopeContainerNode implements AsmNamed, ReturnSafeNode {

	private String asmName;

	@Override
	public String getAsmName() {
		return asmName;
	}

	@Override
	public void setAsmName(String asmName) {
		this.asmName = asmName;
	}

	@Override
	public boolean isReturnSafe() {
		boolean returnSafe = false;

		for (Node n : this) {
			if (n instanceof ReturnSafeNode) {
				returnSafe |= ((ReturnSafeNode) n).isReturnSafe();
			}

			if (returnSafe && CompilerOptions.THROW_UNREACHABLE_CODE) {
				throw new RuntimeException(new CompilerException("Code is unreachable: " + n.toString(0)));
			}
		}

		return returnSafe;
	}

}

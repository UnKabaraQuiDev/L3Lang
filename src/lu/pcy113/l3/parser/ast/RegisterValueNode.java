package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class RegisterValueNode extends ExprNode {

	private String reg;
	private boolean dec, int_;

	public RegisterValueNode(String r, boolean decimal, boolean integer) {
		this.reg = r;
		this.dec = decimal;
		this.int_ = integer;
	}

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg;
	}

	@Override
	public boolean isDecimal() throws CompilerException {
		return dec;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return int_;
	}

}

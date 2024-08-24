package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.expr.ExprNode;

public class RegisterValueNode extends ExprNode {

	private String reg;
	private boolean double_, int_, float_;

	public RegisterValueNode(String r, boolean double_, boolean float_, boolean integer) {
		this.reg = r;
		this.double_ = double_;
		this.int_ = integer;
		this.float_ = float_;
	}

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg;
	}

	@Override
	public boolean isDouble() throws CompilerException {
		return double_;
	}

	@Override
	public boolean isFloat() throws CompilerException {
		return float_;
	}

	@Override
	public boolean isInteger() throws CompilerException {
		return int_;
	}

}

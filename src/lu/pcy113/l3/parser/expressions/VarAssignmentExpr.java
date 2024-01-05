package lu.pcy113.l3.parser.expressions;

import lu.pcy113.l3.lexer.tokens.Token;

public class VarAssignmentExpr extends VarExpr {
	
	private Token var;
	private Token ident;
	
	private boolean declaration;
	private ValueExpr expression;
	
	public VarAssignmentExpr(Token var, Token ident) {
		this.var = var;
		this.ident = ident;
		this.declaration = true;
	}
	public VarAssignmentExpr(Token var, Token ident, ValueExpr expression) {
		this.var = var;
		this.ident = ident;
		this.declaration = false;
		this.expression = expression;
	}
	
	public Token getVar() {
		return var;
	}
	public Token getIdent() {
		return ident;
	}
	public boolean isDeclaration() {
		return declaration;
	}
	
}

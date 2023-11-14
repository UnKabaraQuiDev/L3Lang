package lu.pcy113.l3.parser.expressions.containers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.expressions.Expr;
import lu.pcy113.l3.parser.expressions.FunctionExpr;
import lu.pcy113.l3.parser.expressions.VariableExpr;

public class FunctionBodyExprContainer extends ExprContainer implements FunctionExpr {

	private Token returnType;
	private String identifier;
	private Expr[] parameters;
	
	public FunctionBodyExprContainer(Token returnType, String identifier, List<Expr> params) {
		this.returnType = returnType;
		this.identifier = identifier;
		this.parameters = params.toArray(new Expr[params.size()]);
	}
	
	public Token getReturnType() {return returnType;}
	public String getIdentifier() {return identifier;}
	public Expr[] getParameters() {return parameters;}
	
	@Override
	public String toString() {
		return "ident="+identifier+", params=["+Arrays.stream(parameters).map(e -> e.getClass().getSimpleName()+" {"+e.toString()+"}").collect(Collectors.joining(", "))+"], returnType="+returnType;
	}

}

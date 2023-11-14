package lu.pcy113.l3.parser.expressions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.tokens.Token;

public class FunctionDeclarationExpr extends Expr {
	
	private Token returnType;
	private String identifier;
	private VariableDeclarationExpr[] parameters;
	
	public FunctionDeclarationExpr(Token returnType, String identifier, List<VariableDeclarationExpr> params) {
		this.returnType = returnType;
		this.identifier = identifier;
		this.parameters = params.toArray(new VariableDeclarationExpr[params.size()]);
	}
	
	public Token getReturnType() {return returnType;}
	public String getIdentifier() {return identifier;}
	public VariableDeclarationExpr[] getParameters() {return parameters;}
	
	@Override
	public String toString() {
		return "ident="+identifier+", params=["+Arrays.stream(parameters).map(e -> e.getClass().getSimpleName()+" {"+e.toString()+"}").collect(Collectors.joining(", "))+"] returnType="+returnType;
	}
	
}

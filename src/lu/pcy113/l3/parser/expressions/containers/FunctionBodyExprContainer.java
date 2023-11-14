package lu.pcy113.l3.parser.expressions.containers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.pcy113.l3.lexer.tokens.Token;
import lu.pcy113.l3.parser.expressions.VariableDeclarationExpr;

public class FunctionBodyExprContainer extends ExprContainer {

	private Token returnType;
	private String identifier;
	private VariableDeclarationExpr[] parameters;
	
	public FunctionBodyExprContainer(Token returnType, String identifier, List<VariableDeclarationExpr> params) {
		this.returnType = returnType;
		this.identifier = identifier;
		this.parameters = params.toArray(new VariableDeclarationExpr[params.size()]);
	}
	
	public Token getReturnType() {return returnType;}
	public String getIdentifier() {return identifier;}
	public VariableDeclarationExpr[] getParameters() {return parameters;}
	
	@Override
	public String toString() {
		return "ident="+identifier+", params=["+Arrays.stream(parameters).map(e -> e.getClass().getSimpleName()+" {"+e.toString()+"}").collect(Collectors.joining(", "))+"], returnType="+returnType;
	}

}

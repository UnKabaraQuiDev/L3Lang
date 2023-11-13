package lu.pcy113.l3.parser;

import java.util.ArrayList;
import java.util.List;

public class ExpressionContainer extends Expression {
	
	protected List<Expression> expressions = new ArrayList<>();
	
	public void add(VariableAllocation variableVariableDeclaration) {
		expressions.add(variableVariableDeclaration);
	}

}

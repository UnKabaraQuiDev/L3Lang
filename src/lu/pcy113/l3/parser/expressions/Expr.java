package lu.pcy113.l3.parser.expressions;

import java.io.PrintStream;

import lu.pcy113.l3.parser.expressions.containers.ExprContainer;

public class Expr {
	
	protected ExprContainer parent = null;
	
	public void print(int index, PrintStream out, int tabCount) {
		String tabs = "";
		for(int i = 0; i < tabCount; i++)
			tabs += "\t";
		
		out.println(tabs+index+". "+getClass().getSimpleName()+"["+toString()+"]");
	}
	
	public ExprContainer getParent() {return parent;}
	public void setParent(ExprContainer parent) {this.parent = parent;}
	
}

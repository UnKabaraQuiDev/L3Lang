package lu.pcy113.l3.parser.expressions;

public class Expr {
	
	protected ExprContainer parent = null;
	
	public String toString(int tabs) {
		System.out.println("sub: "+tabs);
		
		String str = "";
		for(int i = 0; i < tabs; i++)
			str += "\t";
		str += getClass().getSimpleName()+"["+toString()+"]";
		
		return str;
	}
	
	public ExprContainer getParent() {return parent;}
	public void setParent(ExprContainer parent) {this.parent = parent;}
	
}

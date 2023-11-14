package lu.pcy113.l3.parser.expressions.containers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lu.pcy113.l3.parser.expressions.Expr;

public class ExprContainer extends Expr {
	
	protected List<Expr> children = new ArrayList<>();
	
	public void addAll(Collection<Expr> collection) {
		children.addAll(collection);
	}
	public void add(Expr expr) {
		children.add(expr);
		expr.setParent(this);
	}
	
	public List<Expr> getChildren() {return Collections.unmodifiableList(children);}
	
	public ExprContainer getOriginalParent() {
		ExprContainer p = this;
		while(p.getParent() != null) {
			p = p.getParent();
		}
		return p;
	}
	
	@Override
	public void print(int index, PrintStream out, int tabCount) {
		String tab="";
		for(int i = 0; i < tabCount; i++)
			tab += "\t";
		
		out.println(tab+index+". "+getClass().getSimpleName()+"("+toString()+") {");
		if(!children.isEmpty()) {
			int i = 0;
			for(Expr e : children) {
				e.print(i++, out, tabCount+1);
				//out.println(tab++"."+e.toString(tabs+1)+",");
			}
		}
		out.println(tab+"}");
	}
	
}

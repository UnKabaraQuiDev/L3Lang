package lu.pcy113.l3.parser.expressions;

import java.util.ArrayList;
import java.util.List;

public class ExprContainer extends Expr {
	
	protected List<Expr> children = new ArrayList<>();
	
	public void add(Expr expr) {
		children.add(expr);
		expr.setParent(this);
	}
	
	public List<Expr> getChildren() {return children;}
	
	public ExprContainer getOriginalParent() {
		ExprContainer p = this;
		while(p.getParent() != null) {
			p = p.getParent();
		}
		return p;
	}
	
	@Override
	public String toString(int tabs) {
		System.out.println("par: "+tabs);
		
		String str = "", childr = "";
		str += "\n";
		for(int i = 0; i < tabs; i++)
			str += "\t";
		
		if(!children.isEmpty()) {
			int i = 0;
			for(Expr e : children) {
				childr += (i++)+". "+e.toString(tabs+1)+",\n";
			}
		}
		
		str += getClass().getSimpleName()+" {\n";
		str += childr;
		str += "}";
		
		return str;
	}
	
}

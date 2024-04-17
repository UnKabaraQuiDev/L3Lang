package lu.pcy113.l3.parser.ast;

public class FunArgsDefNode extends Node {

	public boolean argsEquals(FunArgsValNode args) {
		
		if(this.getChildren().size() != args.getChildren().size()) {
			return false;
		}
		
		for(int i = 0; i < args.getChildren().size(); i++) {
			if(!((FunArgDefNode) this.getChildren().get(i)).getLet().getType().equals(((FunArgValNode) args.getChildren().get(i)).getType())) {
				return false;
			}
		}
		
		return true;
		
	}

}

package lu.pcy113.l3.parser.ast;

import java.util.Arrays;

import lu.pcy113.l3.compiler.CompilerException;

public class FunDefParamsNode extends Node {

	public FunDefParamsNode(FunDefParamNode... defNodes) {
		Arrays.stream(defNodes).forEach(this::add);
	}

	public boolean paramsEquals(FunCallParamsNode args) throws CompilerException {

		if (this.getChildren().size() != args.getChildren().size()) {
			return false;
		}

		for (int i = 0; i < args.getChildren().size(); i++) {
			if(!getParam(i).getType().typeMatches(args.getParam(i))) {
				return false;
			}
		}

		return true;

	}
	
	public FunDefParamNode getParam(int i) {
		return (FunDefParamNode) children.get(i);
	}

}

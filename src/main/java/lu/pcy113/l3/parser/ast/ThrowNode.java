package lu.pcy113.l3.parser.ast;

public class ThrowNode extends Node implements ReturnSafeNode {

	@Override
	public boolean isReturnSafe() {
		return true;
	}

}

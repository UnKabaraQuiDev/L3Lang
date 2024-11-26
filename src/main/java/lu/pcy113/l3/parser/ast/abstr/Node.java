package lu.pcy113.l3.parser.ast.abstr;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;

public abstract class Node implements JSONConvertible {

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject().put("type", this.getClass().getSimpleName());
	}
	
	@Override
	public String toString() {
		return toJSONObject().toString(4);
	}

}

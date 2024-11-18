package lu.pcy113.l3.parser.ast;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import lu.pcy113.l3.impl.JSONConvertible;

public class Node implements JSONConvertible {

	private Node parent;

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject();
	}

}

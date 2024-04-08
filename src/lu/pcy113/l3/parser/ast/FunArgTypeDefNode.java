package lu.pcy113.l3.parser.ast;

import lu.pcy113.l3.lexer.tokens.IdentifierToken;
import lu.pcy113.l3.lexer.tokens.Token;

public class FunArgTypeDefNode extends Node {

	private int argIndex;
	private Token type;
	private IdentifierToken ident;
	private boolean iArray;
	private int arraySize;

	public FunArgTypeDefNode(int argIndex, Token type, IdentifierToken ident, boolean iArray, int arraySize) {
		this.type = type;
		this.ident = ident;
		this.iArray = iArray;
		this.arraySize = arraySize;
		this.argIndex = argIndex;
	}

	public int getArgIndex() {
		return argIndex;
	}
	
	public Token getType() {
		return type;
	}

	public IdentifierToken getIdent() {
		return ident;
	}

	public boolean isiArray() {
		return iArray;
	}

	public int getArraySize() {
		return arraySize;
	}

	
	@Override
	public String toString() {
		return super.toString()+"("+argIndex+", "+type+", "+ident+")";
	}
}

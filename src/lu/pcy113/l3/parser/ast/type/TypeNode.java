package lu.pcy113.l3.parser.ast.type;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.parser.ast.Node;

public abstract class TypeNode extends Node {

	public abstract int getBytesSize() throws CompilerException;

}

package lu.pcy113.l3.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.l3.lexer.TokenType;
import lu.pcy113.l3.parser.ast.BinaryOpNode;
import lu.pcy113.l3.parser.ast.FunArgNumLitValueNode;
import lu.pcy113.l3.parser.ast.FunCallNode;
import lu.pcy113.l3.parser.ast.LetTypeDefNode;
import lu.pcy113.l3.parser.ast.Node;
import lu.pcy113.l3.parser.ast.NumLitNode;
import lu.pcy113.l3.parser.ast.RuntimeNode;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.l3.utils.MemorySize;
import lu.pcy113.pclib.Pair;
import lu.pcy113.pclib.Pairs;

public class X86Compiler extends L3Compiler {

	private int heapSize = 2048;

	public X86Compiler(RuntimeNode env, String outPath) {
		super(env, outPath);
	}

	@Override
	public void compile() throws CompilerException {
		createFile();
		fw = createWriter();

		writeln("section .text");
		writeinstln("global _start");
		writeln("_start:");

		compile(root);

		appendData();

		flushAndClose();

		File dir = outFile.getAbsoluteFile().getParentFile();
		try {
			exec("nasm -f elf32 " + outFile.getPath(), dir);
			exec("ld -m elf_i386 -o " + FileUtils.removeExtension(outFile.getName()) + " " + FileUtils.replaceExtension(outFile.getName(), "o"), dir);
			exec("./" + FileUtils.removeExtension(outFile.getName()), dir);
		} catch (IOException | InterruptedException e) {
			throw new CompilerException("Could not exec: '" + outFile + "', '" + FileUtils.removeExtension(outFile.getName()) + "' and '" + FileUtils.replaceExtension(outFile.getName(), "o") + "' in " + dir, e);
		}
	}

	private List<Pair<Integer, ScopeVarDefinition>> scopeVarDefinitions = new ArrayList<>();
	private int scopeLevel = 0;

	private List<Pair<Integer, ScopeVarDefinition>> getScopeVarDefinitons(String codeName) {
		List<Pair<Integer, ScopeVarDefinition>> ll = new ArrayList<Pair<Integer, ScopeVarDefinition>>();
		for (Pair<Integer, ScopeVarDefinition> isvd : scopeVarDefinitions) {
			if (isvd.getValue().getCodeName().equals(codeName)) {
				ll.add(isvd);
			}
		}
		return ll;
	}

	private void compile(Node node) throws CompilerException {
		if (node instanceof RuntimeNode) {
			for (Node n : node.getChildren()) {
				compile(n);
			}
		} else if (node instanceof FunCallNode) {
			compileFunCallNode((FunCallNode) node);
		} else if (node instanceof LetTypeDefNode) {
			compileLetTypeDefNode((LetTypeDefNode) node);
		}
	}

	private void compileLetTypeDefNode(LetTypeDefNode node) throws CompilerException {
		if (node.isiStatic()) {
			List<Pair<Integer, ScopeVarDefinition>> svd = getScopeVarDefinitons(node.getIdent().getIdentifier());
			for (Pair<Integer, ScopeVarDefinition> isvd : svd) {
				if (isvd.getValue().getCodeName().equals(node.getIdent().getIdentifier())) {
					throw new CompilerException("Variable: " + node.getIdent().getIdentifier() + ", already defined: " + isvd.getValue().getNode().getIdent());
				}
			}

			ScopeVarDefinition sd = new ScopeVarDefinition(node, node.getIdent().getIdentifier(), newVar(), true);
			scopeVarDefinitions.add(Pairs.pair(scopeLevel, sd));

			
			if (node.getType().getType().softEquals(TokenType.TYPE)) { // generic type
				String typeSize = getTypeNameBySize(MemorySize.getBytes(node.getType().getType()));
				if(typeSize == null) {
					throw new CompilerException("Cannot declare static, generic variable of type: "+node.getType());
				}
				writedataln(sd.getAsmName() + " " + typeSize + " 0  ; " + node.getType().getType().getStringValue() + " " + node.getIdent().getIdentifier() + " at " + node.getIdent().getLine() + ":" + node.getIdent().getColumn());
			}
		}
	}

	private void compileFunCallNode(FunCallNode node) throws CompilerException {
		if (node.isPreset()) {
			if (node.getName().getIdentifier().equals("exit")) {
				writeinstln("; Exit program");
				writeinstln("mov eax, 1 ; Syscall exit");
				Node arg0 = node.getChildren().get(0);
				if (arg0 instanceof FunArgNumLitValueNode) {
					long exitValue = 0;
					Node arg0node = ((FunArgNumLitValueNode) arg0).getValue();
					if(arg0node instanceof BinaryOpNode) {
						// TODO
					}else if(arg0node instanceof NumLitNode) {
						exitValue = (long) ((NumLitNode) arg0node).getValue();
					}
					writeinstln("mov ebx, " + exitValue + " ; Syscall exit code");
				} else {
					assert false;
				}
				writeinstln("int 0x80   ; Syscall call");
			}
		}
	}
	
	private String getTypeNameBySize(int bytes) {
		switch (bytes) {
		case 1:
			return "db";
		case 2:
			return "dw";
		case 4:
			return "dd";
		case 8:
			return "dq";
		}
		return null;
	}

}

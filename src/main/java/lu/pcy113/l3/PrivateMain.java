package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.compiler.llvm.LwjglLlvmCompiler;
import lu.pcy113.l3.compiler.llvm.NativeExecutableLinker;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;

import lu.kbra.pclib.logger.GlobalLogger;

public class PrivateMain {

	public static void main(final String[] args)
			throws FileNotFoundException, IOException, LexerException, L3Exception {
		GlobalLogger.init(new File("./config/logs.properties"));

		final File l3Dir = new File("./l3/");
		final File srcDir = new File(l3Dir, "src/");
		final File binDir = new File(l3Dir, "bin/");

		final String mainFile = args.length > 0 ? args[0] : "lu/lang/base/Test.l3";
		final String otherFile = args.length > 1 ? args[1] : "sys/sysout.l3";

		final FileNode mainNode = PrivateMain.parse(srcDir, mainFile);
		System.out.println(mainNode.toJSONObject().toString(4));

		final RuntimeNode runtime;
		if (otherFile != null) {
			final FileNode otherNode = PrivateMain.parse(srcDir, otherFile);
			System.out.println(otherNode.toJSONObject().toString(4));
			runtime = new RuntimeNode(mainNode, Arrays.asList(otherNode));
		} else {
			runtime = new RuntimeNode(mainNode);
		}

		final LwjglLlvmCompiler compiler = new LwjglLlvmCompiler(runtime, binDir);
		compiler.compile();
		System.out.println("LLVM IR: " + compiler.getOutFileIr());
		System.out.println("Object file: " + compiler.getOutFileObj());

		final NativeExecutableLinker linker = new NativeExecutableLinker();
		final File executableFile = linker.link(compiler.getOutFileObj());
		System.out.println("Executable file: " + executableFile);
	}

	private static FileNode parse(final File srcDir, final String path) throws FileNotFoundException, IOException {
		final L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, path)));
		lexer.lexe();
		final L3Parser parser = new L3Parser(lexer.iterator(), path);
		parser.parse();
		return parser.getFile();
	}

}

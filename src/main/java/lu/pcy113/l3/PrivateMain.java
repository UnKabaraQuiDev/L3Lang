package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.x86.X86Compiler;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ParserException;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.l3.utils.FileUtils;
import lu.pcy113.pclib.logger.GlobalLogger;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, ParserException, CompilerException, L3Exception {
		GlobalLogger.init(new File("./config/logs.properties"));
		// GlobalLogger.getLogger().addCallerWhiteList(PrivateMain.class.getName());
		// GlobalLogger.getLogger().addCallerWhiteList(L3Parser.class.getName());

		System.out.println(Arrays.toString(new File("./").list()));

		File l3Dir = new File("./l3/");
		File srcDir = new File(l3Dir, "src/");
		File binDir = new File(l3Dir, "bin/");

		/*L3Lexer lexer = new L3Lexer(new FileReader(new File("./expr.l3")));
		lexer.lexe();
		L3ExprParser exprParser = new L3ExprParser(lexer);
		exprParser.parse();
		System.out.println(exprParser.getRoot().toString(0));*/
		
		String mainFile = "lu/lang/base/Test.l3";

		L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, mainFile)));
		System.out.println("Input:\n" + lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);

		L3Parser parser = new L3Parser(mainFile, lexer);
		parser.parse();

		FileNode fileNode = parser.getRoot();
		fileNode.setMain(true);
		// fileNode.containsMainFunDescriptor();

		RuntimeNode runtimeNode = new RuntimeNode(fileNode);

		System.out.println(runtimeNode.toString(0));
		File astFile = new File(binDir, FileUtils.removeExtension(mainFile) + "-ast.txt");
		createParent(astFile);
		Files.write(Paths.get(astFile.getPath()), runtimeNode.toString(0).getBytes(), StandardOpenOption.CREATE);
		
		X86Compiler compiler = new X86Compiler(runtimeNode, binDir, mainFile);
		compiler.compile();
	}

	private static void createParent(File file) {
		file.getParentFile().mkdirs();
	}

}

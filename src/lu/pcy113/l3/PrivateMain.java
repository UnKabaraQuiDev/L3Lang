package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3ExprParser;
import lu.pcy113.l3.parser.ParserException;
import lu.pcy113.pclib.GlobalLogger;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, ParserException, CompilerException, L3Exception {
		GlobalLogger.init(new File("./config/logs.properties"));

		System.out.println(Arrays.toString(new File("./").list()));

		File l3Dir = new File("./l3/");
		File srcDir = new File(l3Dir, "src/");

		L3Lexer lexer = new L3Lexer(new FileReader(new File("./expr.l3")));
		lexer.lexe();
		L3ExprParser exprParser = new L3ExprParser(lexer);
		exprParser.parse();
		System.out.println(exprParser.getRoot().toString(0));
		
		/*String mainFile = "test.l3";

		L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, mainFile)));
		System.out.println("Input:\n" + lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);

		L3Parser parser = new L3Parser(mainFile, lexer);
		parser.parse();

		FileNode fileNode = parser.getRoot();
		fileNode.containsMainFunDescriptor();

		RuntimeNode runtimeNode = new RuntimeNode(fileNode);

		System.out.println(runtimeNode.toString(0));
		Files.write(Paths.get(l3Dir + "/" + FileUtils.removeExtension(mainFile) + "-ast.txt"), runtimeNode.toString(0).getBytes());
		
		X86Compiler compiler = new X86Compiler(runtimeNode, l3Dir, mainFile);
		compiler.compile();*/
	}

}

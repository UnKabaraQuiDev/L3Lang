package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.compiler.L3Compiler;
import lu.pcy113.l3.compiler.x86_64.X86_64Compiler;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ast.container.FileNode;
import lu.pcy113.l3.parser.ast.container.RuntimeNode;
import lu.pcy113.pclib.logger.GlobalLogger;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, L3Exception {
		GlobalLogger.init(new File("./config/logs.properties"));
		// GlobalLogger.getLogger().addCallerWhiteList(PrivateMain.class.getName());
		// GlobalLogger.getLogger().addCallerWhiteList(L3Parser.class.getName());

		System.out.println(Arrays.toString(new File("./").list()));

		File l3Dir = new File("./l3/");
		File srcDir = new File(l3Dir, "src/");
		File binDir = new File(l3Dir, "bin/");

		final String mainFile = "lu/lang/base/Test.l3";
		final String otherFile = "lu/lang/base/Test2.l3";

		L3Parser parser = null;
		try {
			L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, mainFile)));
			System.out.println("Input:\n" + lexer.getInput());
			lexer.lexe();
			lexer.getTokens().forEach(System.out::println);
			
			parser = new L3Parser(lexer.iterator(), mainFile);
			parser.parse();
		} catch (Exception e) {
			throw e;
		}
		
		final FileNode file1 = parser.getFile();
		System.out.println(file1.toJSONObject().toString(4));
		
		try {
			L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, otherFile)));
			System.out.println("Input:\n" + lexer.getInput());
			lexer.lexe();
			lexer.getTokens().forEach(System.out::println);
			
			parser = new L3Parser(lexer.iterator(), otherFile);
			parser.parse();
		} catch (Exception e) {
			throw e;
		}
		
		final FileNode file2 = parser.getFile();
		System.out.println(file2.toJSONObject().toString(4));

		final RuntimeNode runtime = new RuntimeNode(file1, Arrays.asList(file2));
		
		X86_64Compiler compiler = new X86_64Compiler(runtime, binDir);
		compiler.compile();
	}

}

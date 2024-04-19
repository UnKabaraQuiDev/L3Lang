package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.X86Compiler;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ParserException;
import lu.pcy113.l3.parser.ast.scope.FileNode;
import lu.pcy113.l3.parser.ast.scope.RuntimeNode;
import lu.pcy113.pclib.GlobalLogger;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, ParserException, CompilerException, L3Exception {
		GlobalLogger.init(new File("./config/logs.properties"));

		System.out.println(Arrays.toString(new File("./").list()));

		String mainFile = "l3/src/test.l3";
		String sysoutFile = "l3/src/sys/sysout.l3";

		L3Lexer lexer = new L3Lexer(new FileReader(new File(mainFile)));
		System.out.println("Input:\n" + lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);

		L3Parser parser = new L3Parser(mainFile, lexer);
		parser.parse();

		FileNode fileNode = parser.getRoot();
		fileNode.containsMainFunDescriptor();
		
		lexer = new L3Lexer(new FileReader(new File(sysoutFile)));
		System.out.println("Input:\n" + lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);

		parser = new L3Parser(sysoutFile, lexer);
		parser.parse();
		
		FileNode sysoutFileNode = parser.getRoot();
		
		RuntimeNode runtimeNode = new RuntimeNode(fileNode, sysoutFileNode);
		
		System.out.println(runtimeNode.toString(0));
		Files.write(Paths.get(mainFile+"-ast.txt"), runtimeNode.toString(0).getBytes());
		
		X86Compiler compiler = new X86Compiler(runtimeNode, mainFile + "-out.asm");
		compiler.compile();
	}

}

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

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, ParserException, CompilerException {
		GlobalLogger.init(new File("./config/logs.properties"));
		
		System.out.println(Arrays.toString(new File("./").list()));
		
		String file = "expr.l3";
		
		L3Lexer lexer = new L3Lexer(new FileReader(new File(file)));
		System.out.println("Input:\n"+lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);
		
		L3ExprParser parser = new L3ExprParser(lexer);
		parser.parse();
		System.out.println(parser.getRoot().toString(0));
		
		/*L3Parser parser = new L3Parser(lexer);
		parser.parse();
		System.out.println(parser.getRoot().toString(0));
		
		X86Compiler compiler = new X86Compiler(parser.getRoot(), file+"-out.asm");
		compiler.compile();*/
	}

}

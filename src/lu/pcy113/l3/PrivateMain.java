package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.compiler.CompilerException;
import lu.pcy113.l3.compiler.X86Compiler;
import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ParserException;
import lu.pcy113.l3.parser.ast.containers.EnvContainer;
import lu.pcy113.l3.parser.ast.containers.FileContainer;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException {
		System.out.println(Arrays.toString(new File("./").list()));
		
		String file = "test.l3";
		
		L3Lexer lexer = new L3Lexer(new FileReader(new File(file)));
		System.out.println("Input:\n"+lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);
		
		L3Parser parser = new L3Parser(lexer);
		EnvContainer env = new EnvContainer();
		FileContainer fc = new FileContainer(file);
		env.add(fc);
		try {
			parser.parse(fc);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		fc.print(0, System.out, 0);
		
		X86Compiler compiler = new X86Compiler(env, System.err);
		try {
			compiler.compile();
		} catch (CompilerException e) {
			e.printStackTrace();
		}
	}

}

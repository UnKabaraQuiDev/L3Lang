package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
import lu.pcy113.l3.parser.ParserException;

public class PrivateMain {

	public static void main(String[] args) throws FileNotFoundException, IOException, LexerException, ParserException {
		System.out.println(Arrays.toString(new File("./").list()));
		
		L3Lexer lexer = new L3Lexer(new FileReader(new File("./main.l3")));
		System.out.println("Input:\n"+lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);
		
		L3Parser parser = new L3Parser(lexer);
		parser.parse();
	}

}

package lu.pcy113.l3.lexer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class L3LexerMain {

	public static void main(String[] args) throws IOException {
		if(args.length < 1)
			throw new IllegalArgumentException("Missing file input to tokenize.");
		
		File file = new File(args[0]);
		if(!file.exists())
			throw new IllegalArgumentException("Target file '"+args[0]+"' does not exists.");
		
		L3Lexer lexer = new L3Lexer(new FileReader(file));
	}

}

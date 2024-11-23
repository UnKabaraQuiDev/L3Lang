package lu.pcy113.l3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import lu.pcy113.l3.lexer.L3Lexer;
import lu.pcy113.l3.lexer.LexerException;
import lu.pcy113.l3.parser.L3Parser;
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

		String mainFile = "lu/lang/base/Test.l3";

		L3Lexer lexer = new L3Lexer(new FileReader(new File(srcDir, mainFile)));
		System.out.println("Input:\n" + lexer.getInput());
		lexer.lexe();
		lexer.getTokens().forEach(System.out::println);

		L3Parser parser = null;
		try {
			parser = new L3Parser(lexer.iterator(), mainFile);
			parser.parse();
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(parser.getFile().toJSONObject().toString(4));
	}

	private static void createParent(File file) {
		file.getParentFile().mkdirs();
	}

}

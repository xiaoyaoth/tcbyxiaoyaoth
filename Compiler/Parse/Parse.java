package Parse;

import Semant.Semant;
import Translate.Level;

public class Parse {

	public ErrorMsg.ErrorMsg errorMsg;
	public Absyn.Print print = new Absyn.Print(System.out);

	public Parse(String filename) {
		errorMsg = new ErrorMsg.ErrorMsg(filename);
		java.io.InputStream inp;
		try {
			inp = new java.io.FileInputStream(filename);
		} catch (java.io.FileNotFoundException e) {
			throw new Error("File not found: " + filename);
		}
		parser parser = new parser(new Yylex(inp, errorMsg), errorMsg);
		Semant s = new Semant(errorMsg);
		//parser parser = new parser(new Yylex(System.in,errorMsg), errorMsg);

		try {
			parser./* debug_ */parse();
			print.prExp(parser.parseResult, 0);
			System.out.println();
			s.transProg(parser.parseResult);
			System.out.println("Succeed");

		} catch (Throwable e) {
			e.printStackTrace();
			throw new Error(e.toString());
		} finally {
			try {
				inp.close();
			} catch (java.io.IOException e) {
			}
		}
	}
}

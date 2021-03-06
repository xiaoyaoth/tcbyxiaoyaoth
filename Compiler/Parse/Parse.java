package Parse;

import Semant.Semant;

public class Parse {

	public ErrorMsg.ErrorMsg errorMsg;
	public Absyn.Print printAbsyn = new Absyn.Print(System.out);
	public Tree.Print printIR = new Tree.Print(System.out);

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

		try {
			parser./* debug_ */parse();
			System.out.println("****** Abstract Syntax Tree ******");
			printAbsyn.prExp(parser.parseResult, 0);
			System.out.println("\n************ end ************");
			System.out.println();
			
			s.transProg(parser.parseResult);
			//System.out.println(errorMsg.anyErrors);
			if (errorMsg.anyErrors == false) {
				System.out.println(filename + " succeed");
			}else
				System.out.println(filename + " error(s) dectected");
			System.out.println();
		} catch (Throwable e) {
			System.out.println(filename + " errors");
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

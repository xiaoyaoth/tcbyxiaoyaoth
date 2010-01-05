package Semant;

import Symbol.Symbol;
import Symbol.Table;
import Temp.*;
import Types.*;
import Util.*;
import Translate.*;


class Env {
	Table vEnv;
	Table tEnv;
	ErrorMsg.ErrorMsg error;
	
	Env(ErrorMsg.ErrorMsg err) {
		error = err;
		initTEnv();
		initVEnv();
	}
	void initTEnv(){
		tEnv = new Table();
		tEnv.put(Symbol.symbol("int"), new INT());
		tEnv.put(Symbol.symbol("string"),new STRING());
	}
	public void initVEnv(){
		vEnv = new Table();		
		Symbol sym;
		RECORD formals;
		Type result;
		
		sym = Symbol.symbol("print");
		formals = new Types.RECORD(Symbol.symbol("s"), new Types.STRING(), null);
		result = new Types.VOID();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("printi");
		formals = new Types.RECORD(Symbol.symbol("i"), new Types.INT(), null);
		result = new Types.VOID();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("flush");
		result = new VOID();
		vEnv.put(sym, new StdFuncEntry(null, result));

		sym = Symbol.symbol("getchar");
		result = new STRING();
		vEnv.put(sym, new StdFuncEntry(null, result));
		
		sym = Symbol.symbol("ord");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), null);
		result = new INT();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("chr");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new STRING();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("size");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), null);
		result = new INT();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("substring");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), 
					new RECORD(Symbol.symbol("f"), new INT(),
						new RECORD(Symbol.symbol("i"), new INT(), null)));
		result = new STRING();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("concat");
		formals = new Types.RECORD(Symbol.symbol("s1"), new STRING(), 
					new RECORD(Symbol.symbol("s2"), new STRING(),null));
		result = new STRING();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("not");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new INT();
		vEnv.put(sym, new StdFuncEntry(formals, result));

		sym = Symbol.symbol("exit");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new VOID();
		vEnv.put(sym, new StdFuncEntry(formals, result));
	}
}

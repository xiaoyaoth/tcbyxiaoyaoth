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
	Translate.Level root;
	Env(ErrorMsg.ErrorMsg err, Level root) {
		error = err;
		this.root = root;
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
		Translate.Level level = root;
		
		sym = Symbol.symbol("print");
		formals = new Types.RECORD(Symbol.symbol("s"), new Types.STRING(), null);
		result = new Types.VOID();
		level = new Translate.Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("printi");
		formals = new Types.RECORD(Symbol.symbol("i"), new Types.INT(), null);
		result = new Types.VOID();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("flush");
		formals = new Types.RECORD(null, new VOID(), null);
		result = new VOID();
		level = new Level(root, sym, null);
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("getchar");
		formals = new Types.RECORD(null, new VOID(), null);
		result = new STRING();
		level = new Level(root, sym, null);
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("ord");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), null);
		result = new INT();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("chr");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new STRING();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("size");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), null);
		result = new INT();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("substring");
		formals = new Types.RECORD(Symbol.symbol("s"), new STRING(), 
					new RECORD(Symbol.symbol("f"), new INT(),
						new RECORD(Symbol.symbol("i"), new INT(), null)));
		result = new STRING();
		level = new Level(root, sym, new BoolList(true, new BoolList(true, new BoolList(true, null))));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("concat");
		formals = new Types.RECORD(Symbol.symbol("s1"), new STRING(), 
					new RECORD(Symbol.symbol("s2"), new STRING(),null));
		result = new STRING();
		level = new Level(root, sym, new BoolList(true, new BoolList(true, null)));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("not");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new INT();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		sym = Symbol.symbol("exit");
		formals = new Types.RECORD(Symbol.symbol("i"), new INT(), null);
		result = new VOID();
		level = new Level(root, sym, new BoolList(true, null));
		vEnv.put(sym, new StdFuncEntry(formals, result, new Label(sym), level));
		
		
		
	}
}

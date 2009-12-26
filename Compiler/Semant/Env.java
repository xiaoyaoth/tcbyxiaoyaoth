package Semant;

import Symbol.Symbol;
import Symbol.Table;
import Temp.*;
import Types.*;
import Util.*;


class Env {
	Table vEnv = null;
	Table tEnv = null;
	ErrorMsg.ErrorMsg errorMsg = null;
	Env(ErrorMsg.ErrorMsg err) {
		errorMsg = err;
	}
	void initTEnv(){
		tEnv = new Table();
		tEnv.put(Symbol.symbol("int"), new INT());
		tEnv.put(Symbol.symbol("string"),new STRING());
	}
	public void initVEnv(){
		vEnv = new Table();
		Symbol sym = null;
		RECORD formals = null;
		Type result = null;
		
		sym = Symbol.symbol("print");
		formals = new RECORD(Symbol.symbol("s"), new STRING(), null);
	}
}

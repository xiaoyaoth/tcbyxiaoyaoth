package Semant;

import Temp.Label;
import Translate.Level;
import Types.RECORD;
import Types.Type;

abstract class Entry {
}

class VarEntry extends Entry {
	Types.Type ty;
	Translate.Access acc;
	VarEntry(Translate.Access a, Types.Type t){
		acc = a;
		ty = t;
	}
}

class LoopVarEntry extends VarEntry{
	LoopVarEntry(Translate.Access a, Types.Type t){
		super(a, new Types.INT());
	}	
}

class FuncEntry extends Entry {
	Types.RECORD formals;
	Types.Type result;
	Temp.Label label;
	Translate.Level level;
	public FuncEntry(Translate.Level lv, Temp.Label lb, Types.RECORD f, Types.Type r){
		formals = f;
		result = r;
		label = lb;
		level = lv;
	}
}

class StdFuncEntry extends FuncEntry{
	public StdFuncEntry(Translate.Level lv, Temp.Label lb, RECORD f, Type r) {
		super(lv, lb, f, r);
		// TODO Auto-generated constructor stub
	}	
}



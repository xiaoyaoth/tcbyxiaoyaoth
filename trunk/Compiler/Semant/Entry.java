package Semant;

import Temp.Label;
import Translate.Level;
import Types.RECORD;
import Types.Type;

abstract class Entry {
}

class VarEntry extends Entry {
	Types.Type ty;
	Translate.Access access;
	VarEntry(Translate.Access acc,Types.Type t){
		ty = t;
		access = acc;
	}
}

class LoopVarEntry extends VarEntry{
	LoopVarEntry(Translate.Access a, Types.Type t){
		super(a, new Types.INT());
		access = a;
		ty = new Types.INT();
	}
	
}

class FuncEntry extends Entry {
	Types.RECORD formals;
	Types.Type result;
	Temp.Label label;
	Translate.Level level;
	public FuncEntry(Types.RECORD f, Types.Type r, Temp.Label l, Translate.Level le){
		formals = f;
		result = r;
		label = l;
		level = le;
	}
}

class StdFuncEntry extends FuncEntry{
	public StdFuncEntry(RECORD f, Type r, Label l, Level le) {
		super(f, r, l, le);
		// TODO Auto-generated constructor stub
	}	
}



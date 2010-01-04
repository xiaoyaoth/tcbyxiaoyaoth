package Semant;

import Temp.Label;
import Translate.Level;
import Types.RECORD;
import Types.Type;

abstract class Entry {
}

class VarEntry extends Entry {
	Types.Type ty;
	VarEntry(Types.Type t){
		ty = t;
	}
}

class LoopVarEntry extends VarEntry{
	LoopVarEntry( Types.Type t){
		super(new Types.INT());
		//access = a;
		ty = new Types.INT();
	}
	
}

class FuncEntry extends Entry {
	Types.RECORD formals;
	Types.Type result;
	//Temp.Label label;
	//Translate.Level level;
	public FuncEntry(Types.RECORD f, Types.Type r){
		formals = f;
		result = r;
		//label = l;
		//level = le;
	}
}

class StdFuncEntry extends FuncEntry{
	public StdFuncEntry(RECORD f, Type r) {
		super(f, r);
		// TODO Auto-generated constructor stub
	}	
}



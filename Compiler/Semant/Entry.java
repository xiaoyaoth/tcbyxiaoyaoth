package Semant;

abstract class Entry {
}

class VarEntry extends Entry {
	Types.Type ty;
	VarEntry(Types.Type t){
		ty = t;
	}
}

class FunEntry extends Entry {
	Types.RECORD formals;
	Types.Type result;
	public FunEntry(Types.RECORD f, Types.Type r){
		formals = f;
		result = r;
	}
}



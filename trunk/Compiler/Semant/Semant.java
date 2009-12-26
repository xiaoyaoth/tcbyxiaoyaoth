package Semant;

import Absyn.Exp;
import Absyn.Ty;

public class Semant {
	Env env;

	public Semant(ErrorMsg.ErrorMsg err) {
		this(new Env(err));
	}
	
	Semant (Env e){
		env = e;
	}
	
	ExpTy transVar(Absyn.Var e){

	}
	ExpTy transExp(Absyn.OpExp e){
		ExpTy left = transExp(e.left);
		ExpTy right = transExp(e.right);
	}
	Exp transDec(Absyn.Exp e){}
	Ty transTy(Absyn.Ty e){}
}

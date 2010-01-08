package Translate;

import Temp.Label;
import Tree.*;

public class Ex extends Exp {
	Expr exp = null;
	Ex(Expr e){exp = e;}
	
	@Override
	Stm unCx(Label t, Label f) {
		// TODO Auto-generated method stub
		return new CJUMP(CJUMP.NE, exp, new CONST(0), t, f);
	}

	@Override
	Expr unEx() {
		// TODO Auto-generated method stub
		return exp;
	}

	@Override
	Stm unNx() {
		// TODO Auto-generated method stub
		return new EXP(exp);
	}

}

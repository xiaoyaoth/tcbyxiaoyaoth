package Translate;

import Temp.*;
import Tree.*;

public class WhileExp extends Exp{
	Exp test;
	Exp body;
	Label done;
	
	WhileExp(Exp test, Exp body, Label done){
		this.test = test;
		this.body = body;
		this.done = done;
	}

	@Override
	Stm unCx(Label t, Label f) {
		System.err.println("WhileExp.unCx()");
		return null;
		// TODO Auto-generated method stub
		//return new CJUMP(CJUMP.NE, test.unEx(), new CONST(0), t, f);
	}

	@Override
	Expr unEx() {
		// TODO Auto-generated method stub
		System.err.println("WhileExp.unEx()");
		return null;
	}

	@Override
	Stm unNx() {
		Label begin = new Label();
		Label t = new Label();
		// TODO Auto-generated method stub
		return new SEQ( new LABEL(begin),
				new SEQ(test.unCx(t, done),
				new SEQ(new LABEL(t),
				new SEQ(body.unNx(),
				new SEQ(new LABEL(begin),
				new LABEL(done))))));
	}
}

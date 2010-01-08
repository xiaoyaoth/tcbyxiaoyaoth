package Translate;

import Temp.*;
import Tree.*;

public class ForExp extends Exp {
	Level home;
	Access var;
	Exp low, high;
	Exp body;
	Label done;

	ForExp(Level home, Access var, Exp low, Exp high, Exp body, Label done){
		this.home = home;
		this.var = var;
		this.low = low;
		this.high = high;
		this.body = body;
		this.done = done;
	}
	@Override
	Stm unCx(Label t, Label f) {
		// TODO Auto-generated method stub
		System.err.println("ForExp.unCx()");
		return null;
	}

	@Override
	Expr unEx() {
		// TODO Auto-generated method stub
		System.err.println("ForExp.unEx()");
		return null;
	}

	@Override
	Stm unNx() {
		Access limit = home.allocLocal(true);
		Label begin = new Label();
		Label goOn = new Label();
		// TODO Auto-generated method stub
		return new SEQ(new MOVE(var.access.exp(new TEMP(home.frame.FP())),low.unEx()),
				new SEQ(new MOVE(limit.access.exp(new TEMP(home.frame.FP())), high.unEx()),
				new SEQ(new CJUMP(CJUMP.LE, var.access.exp(new TEMP(home.frame.FP())), limit.access.exp(new TEMP(home.frame.FP())), begin, done),
				new SEQ(new LABEL(begin),
				new SEQ(body.unNx(),
				new SEQ(new CJUMP(CJUMP.LT, var.access.exp(new TEMP(home.frame.FP())), limit.access.exp(new TEMP(home.frame.FP())), goOn, done),
				new SEQ(new LABEL(goOn),
				new SEQ(new MOVE(var.access.exp(new TEMP(home.frame.FP())), new BINOP(BINOP.PLUS, var.access.exp(new TEMP(home.frame.FP())), new CONST(1))),
				new SEQ(new JUMP(begin),
				new LABEL(done))))))))));
	}
}

package Translate;

import Temp.*;
import Tree.*;

public class IfExp extends Exp{
	private Exp test;
	private Exp then_exp;
	private Exp else_exp;
	
	IfExp(Exp test, Exp then_exp, Exp else_exp){
		this.test = test;
		this.then_exp = then_exp;
		this.else_exp = else_exp; 
	}
	@Override
	Stm unCx(Label t, Label f) {
		return new CJUMP(CJUMP.NE, unEx(), new CONST(0), t, f);
		// TODO Auto-generated method stub
	}

	@Override
	Expr unEx() {
		Temp r = new Temp();
		Label join = new Label();
		Label t = new Label();
		Label f = new Label();
		// TODO Auto-generated method stub
		return new ESEQ(new SEQ(test.unCx(t, f),
					new SEQ(new LABEL(t),
					new SEQ(new MOVE(new TEMP(r), then_exp.unEx()),
					new SEQ(new JUMP(join),
					new SEQ(new LABEL(f),
					new SEQ(new MOVE(new TEMP(r), else_exp.unEx()),
					new LABEL(join))))))),
				new TEMP(r));
	}

	@Override
	Stm unNx() {
		Label join = new Label();
		Label t = new Label();
		Label f = new Label();
		// TODO Auto-generated method stub
		if(else_exp == null)
			return new SEQ(test.unCx(t, join),
						new SEQ(new LABEL(t),
						new SEQ(then_exp.unNx(),
						new LABEL(join))));
		else
			return new SEQ(test.unCx(t, f),
						new SEQ(new LABEL(t),
						new SEQ(then_exp.unNx(),
						new SEQ(new JUMP(join), 
						new SEQ(new LABEL(f),
						new SEQ(else_exp.unNx(), 
						new LABEL(join)))))));
	}
}

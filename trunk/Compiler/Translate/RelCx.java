package Translate;

import Absyn.*;
import Temp.Label;
import Tree.*;

public class RelCx extends Cx{
	int oper = 0;
	Exp left = null;
	Exp right = null;
	
	public RelCx(int oper, Exp left, Exp right){
		switch(oper){
		case OpExp.EQ : this.oper = CJUMP.EQ; break;
		case OpExp.NE : this.oper = CJUMP.EQ; break;
		case OpExp.GT : this.oper = CJUMP.GT; break;
		case OpExp.GE : this.oper = CJUMP.GE; break;
		case OpExp.LE : this.oper = CJUMP.LE; break;
		case OpExp.LT : this.oper = CJUMP.LT; break;
		default : throw new Error(oper + " is not a operator");
		}
		this.left = left;
		this.right = right;
	}

	@Override
	Stm unCx(Label t, Label f) {
		// TODO Auto-generated method stub
		return new CJUMP(oper, left.unEx(), right.unEx(), t, f);
	}

}

package Translate;

import Temp.Label;
import Tree.Expr;
import Tree.Stm;

public class Nx extends Exp {
	Stm stm;
	
	Nx(Stm s){stm = s;}
	
	@Override
	Stm unCx(Label t, Label f) {//�޷�����
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Expr unEx() {//�޷�����
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Stm unNx() {
		// TODO Auto-generated method stub
		return stm;
	}

}

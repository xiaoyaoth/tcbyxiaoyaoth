package Mips;

import Frame.Access;
import Temp.Temp;
import Tree.Expr;
import Tree.TEMP;

public class InReg extends Access{
	private Temp reg;
	
	public InReg(){
		reg = new Temp();
	}

	@Override
	public Expr exp(Expr framePtr) {
		// TODO Auto-generated method stub
		return new TEMP(reg);
	}

	@Override
	public Expr expFromStack(Expr stackPtr) {
		// TODO Auto-generated method stub
		return new TEMP(reg);
	}
}

package Mips;

import Frame.Access;
import Tree.*;


public class InFrame extends Access{
	private MipsFrame frame;
	private int offset;
	
	InFrame(int offset, MipsFrame frame){
		this.offset = offset;
		this.frame = frame;
	}

	@Override
	public Expr exp(Expr framePtr) {
		// TODO Auto-generated method stub
		return new MEM(new BINOP(BINOP.PLUS, framePtr, new CONST(offset)));
	}

	@Override
	public Expr expFromStack(Expr stackPtr) {
		// TODO Auto-generated method stub
		return new MEM(new BINOP(BINOP.PLUS, stackPtr, new CONST(offset - frame.allocDown - frame.wordSize())));
	}

}

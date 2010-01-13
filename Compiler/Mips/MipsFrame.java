package Mips;

import java.util.ArrayList;

import Assem.InstrList;
import Frame.*;
import Temp.*;
import Tree.*;
import Util.BoolList;

public class MipsFrame extends Frame {
	int allocDown;
	private int wordSize = 4;

	ArrayList<MOVE> saveArgs = new ArrayList<MOVE>();

	private Temp fp = new Temp();
	private Temp sp = new Temp();
	private Temp ra = new Temp();
	private Temp v0 = new Temp();
	private Temp zero = new Temp();
	private Temp[] a = new Temp[4];
	private Temp[] callerSaves = new Temp[10];
	private Temp[] calleeSaves = new Temp[8];

	public MipsFrame() {
		for (int i = 0; i < 4; i++)
			a[i] = new Temp();
		for (int i = 0; i < 10; i++)
			callerSaves[i] = new Temp();
		for (int i = 0; i < 8; i++)
			calleeSaves[i] = new Temp();
	}

	public MipsFrame(Label name) {
		this.name = name;
		this.formals = null;
	}

	public Temp FP() {
		return fp;
	}

	public Temp RA() {
		return ra;
	}

	public Temp RV() {
		return v0;
	}

	public Temp SP() {
		return sp;
	}

	public Access allocLocal(boolean escape) {
		if (escape) {
			Access acc = new InFrame(allocDown, this);
			allocDown -= wordSize;
			return acc;
		} else
			return new InReg();
	}

	public InstrList codegen(Stm s) {
		return null;
	}

	public Frame newFrame(Label name, BoolList formals) {
		MipsFrame mf = new MipsFrame();
		mf.name = name;
		mf.allocDown = 0;
		TempList argReg = new TempList(a[0], new TempList(a[1], new TempList(
				a[2], new TempList(a[3], null))));

		int count = 0;
		/*
		 * 是这样一个步骤：首先读boolList的每一项（第一项是静态连接）：读到是否为true。
		 * 如果为true就分配一个Access，因为全是true所以都分派到frame里，每分配一个access，offset就减wordsize；
		 * 这时检查argReg是否为null，不为null 添加放入形参的指令，将fp
		 * 放入临时寄存器，取出其中地址，然后将个形参放入寄存器，再将此寄存器值移入地址
		 */
		for (BoolList f = formals; f != null; f = f.tail, argReg = argReg.tail) {
			count++;
			Access acc = mf.allocLocal(f.head);
			mf.formals = new AccessList(acc, mf.formals);
			if (argReg != null)
				mf.saveArgs.add(new MOVE(acc.exp(new TEMP(fp)), new TEMP(
						argReg.head)));
		}
		// System.out.println(name+"'s arguments amount = " + count);
		return mf;
	}

	private int NumOfCalleeSaves = 8;
	private int NumOfCallerSaves = 10;
	public Stm procEntryExit1(Stm body) {
		/* save formals */
		for (int i = 0; i < saveArgs.size(); i++)
			body = new SEQ((MOVE) saveArgs.get(i), body);

		/* save Callee-save & Caller-save Regs */
		Access[] calleeAcc = new Access[NumOfCalleeSaves];
		Access[] callerAcc = new Access[NumOfCallerSaves];
		Access raAcc = allocLocal(true);
		Access fpAcc = allocLocal(true);
		Temp[] calleeTemp = calleeSaves;
		Temp[] callerTemp = callerSaves;
		for (int i = 0; i < NumOfCalleeSaves; i++) {
			calleeAcc[i] = allocLocal(true);
			body = new SEQ(new MOVE(calleeAcc[i].exp(new TEMP(fp)), new TEMP(
					calleeTemp[i])), body);
		}
		for (int i = 0; i < NumOfCallerSaves; i++) {
			callerAcc[i] = allocLocal(true);
			body = new SEQ(new MOVE(callerAcc[i].exp(new TEMP(fp)), new TEMP(
					callerTemp[i])), body);
		}
		/* save ra */
		body = new SEQ(new MOVE(raAcc.exp(new TEMP(fp)), new TEMP(ra)), body);
		/* compute new fp */
		body = new SEQ(new MOVE(new TEMP(fp), new BINOP(BINOP.PLUS,
				new TEMP(sp), new CONST(-allocDown - wordSize))), body);
		/* save fp */
		body = new SEQ(
				new MOVE(fpAcc.expFromStack(new TEMP(sp)), new TEMP(fp)), body);
		/* recover callee & caller */
		calleeTemp = calleeSaves;
		callerTemp = callerSaves;
		for (int i = 0; i < NumOfCalleeSaves; i++)
			body = new SEQ(body, new MOVE(new TEMP(calleeTemp[i]), calleeAcc[i]
					.exp(new TEMP(fp))));
		for (int i = 0; i < NumOfCallerSaves; i++)
			body = new SEQ(body, new MOVE(new TEMP(callerTemp[i]), callerAcc[i]
					.exp(new TEMP(fp))));
		/* recover ra */
		body = new SEQ(body, new MOVE(new TEMP(ra), raAcc.exp(new TEMP(fp))));
		/* recover fp */
		body = new SEQ(body, new MOVE(new TEMP(fp), fpAcc
				.expFromStack(new TEMP(sp))));
		return body;
	}

	public InstrList procEntryExit2(InstrList body) {
		return null;
	}

	public InstrList procEntryExit3(InstrList body) {
		return null;
	}


	public String string(Label label, String values) {
		String instr = label.toString() + ":\n";// instruction;
		instr += ".asciiz \"" + values + "\"\n";
		return instr;
	}

	public int wordSize() {
		return wordSize;
	}

	public String tempMap(Temp t) {
		String s = "";
		if (t == fp)
			s = "$fp";
		if (t == sp)
			s = "$sp";
		if (t == ra)
			s = "$ra";
		if (t == v0)
			s = "$v0";
		if (t == zero)
			s = "$zero";
		for (int i = 0; i < 4; i++)
			if (t == a[i]) {
				s = "$a" + i;
				break;
			}
		for (int i = 0; i < 8; i++)
			if (t == calleeSaves[i]) {
				s = "$s" + i;
				break;
			}
		for (int i = 0; i < 10; i++)
			if (t == callerSaves[i]) {
				s = "$t" + i;
				break;
			}
		if (s.equals(""))
			return null;
		return s;
	}

	@Override
	public Expr externalCall(String funcName, ExpList args) {
		// TODO Auto-generated method stub
		return new CALL(new NAME(new Label(funcName)), args);
	}

	@Override
	public TempList registers() {
		// TODO Auto-generated method stub
		return null;
	}
}

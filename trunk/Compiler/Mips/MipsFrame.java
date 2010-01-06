package Mips;

import java.util.ArrayList;

import Assem.InstrList;
import Frame.Access;
import Frame.AccessList;
import Frame.Frame;
import Temp.Label;
import Temp.Temp;
import Temp.TempList;
import Tree.MOVE;
import Tree.Stm;
import Tree.TEMP;
import Util.BoolList;

public class MipsFrame extends Frame{
	int allocDown;
	private int wordSize = 4;
	//private int AmtOfCalleeSaves = 8;
	//private int AmtOfCallerSaves = 10;
	
	ArrayList<MOVE> saveArgs = new ArrayList<MOVE>();
	
	private Temp fp = new Temp();
	private Temp sp = new Temp();
	private Temp ra = new Temp();
	private Temp v0 = new Temp();
	private Temp zero = new Temp();
	private Temp[] a = new Temp[4];
	private Temp[] callerSaves = new Temp[10];
	private Temp[] calleeSaves = new Temp[8];
	
	public MipsFrame(){
	}
	
	public MipsFrame(Label name){
		this.name = name;
		this.formals = null;
	}

	public Temp FP() {return fp;}
	public Temp RA() {return ra;}
	public Temp RV() {return v0;}
	public Temp SP() {return sp;}
	
	public Access allocLocal(boolean escape) {
		if(escape){
			Access acc = new InFrame(allocDown, this);
			allocDown -= wordSize;
			return acc;
		}else
			return new InReg();
	}
	
	public InstrList codegen(Stm s) {
		return null;
	}
	
	public Frame newFrame(Label name, BoolList formals) {
		MipsFrame mf = new MipsFrame();
		mf.name = name;
		mf.allocDown = 0;
		TempList argReg = new TempList(a[0], new TempList(a[1], new TempList(a[2], new TempList(a[3], null))));
		
		int count = 0;
		for(BoolList f = formals; f!=null; f=f.tail, argReg = argReg.tail){
			count++;
			Access acc = mf.allocLocal(f.head);
			mf.formals = new AccessList(acc, mf.formals);
			if(argReg!=null)
				mf.saveArgs.add(new MOVE(acc.exp(new TEMP(fp)),new TEMP(argReg.head)));
		}
		//System.out.println(name+"'s arguments amount = " + count);
		return mf;
	}
	
	public Stm procEntryExit1(Stm body) {
		return null;
	}
	
	public InstrList procEntryExit2(InstrList body) {
		return null;
	}
	
	public InstrList procEntryExit3(InstrList body) {
		return null;
	}
	
	public TempList registers() {
		TempList tlist = null;
		for(int i = 0; i<10; i++) tlist = new TempList(callerSaves[i], tlist);
		for(int i = 0; i<8; i++) tlist = new TempList(calleeSaves[i], tlist);
		return tlist;
	}
	
	public String string(Label label, String values) {
		String instr = label.toString() + ":\n";//instruction;
		instr +=".asciiz \"" + values +"\"\n";
		return instr;
	}
	
	public int wordSize() {
		return wordSize;
	}
	
	public String tempMap(Temp t) {
		String s = "";
		if(t == fp) s = "$fp";
		if(t == sp) s = "$sp";
		if(t == ra) s = "$ra";
		if(t == v0) s = "$v0";
		if(t == zero) s = "$zero";
		for(int i = 0; i<4; i++) if(t == a[i]) {s="$a"+i; break;}
		for(int i = 0; i<8; i++) if(t == calleeSaves[i]) {s = "$s"+i; break;}
		for(int i = 0; i<10; i++) if(t == callerSaves[i]) {s = "$t" + i; break;}
		if(s.equals(""))return null;
		return s;
	}
}

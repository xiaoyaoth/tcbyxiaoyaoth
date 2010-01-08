package Translate;

/*************************************
 * ��ȫ�����Access
 * ���º�Access��صĸ���������
 * ���羲̬���ӡ���Access���¡�
 */

import java.util.ArrayList;

import Frame.Frame;
import Mips.MipsFrame;
import Tree.*;
import Temp.*;

public class Translate {
	
	private Frame frame = new MipsFrame(new Label("main"));
	private Frag frags = null;
	private Level home;
	int wordSize = 4;
	/*
	 * tool functions
	 */
	public Frag getResult() {
		return frags;
	}
	
	public void addFrag(Frag frag){
		frag.next = frags;
		frags = frag;
	}
	
	
	Exp transIntExp(int value){
		return new Ex(new CONST(value));
	}
	
	Exp transStringExp(String value){
		Label l = new Label();
		addFrag (new DataFrag(l, frame.string(l, value)));
		return new Ex(new NAME(l));
	}
	
	Exp transNilExp(){
		return new Ex(new CONST(0));
	}
	
	Exp transVarExp(Exp e){
		return e;
	}
	
	Exp transCalcExp(int oper, Exp left, Exp right){
		return new Ex(new BINOP(oper, left.unEx(), right.unEx()));
	}
	
	Exp transStringRelExp(int oper, Exp left, Exp right){
		Expr compare  = home.frame.externalCall("stringEqual", new ExpList(left.unEx(), new ExpList(right.unEx(), null)));
		return new RelCx(oper, new Ex(compare), new Ex(new CONST(0)));
	}
	
	Exp transOtherRelExp(int oper, Exp left, Exp right){
		return new RelCx(oper, left, right);
	}
	
	Exp transAssignExp(Exp lvalue, Exp exp){
		return new Nx(new MOVE(lvalue.unEx(), exp.unEx()));
	}
	
	Exp transCallExp(Level home, Level dest, Label name, ArrayList<Exp> argValue){
		ExpList args = null;
		for(int i = argValue.size()-1; i>=0; --i)
			args = new ExpList(argValue.get(i).unEx(),args);
		Level lv = home;
		Expr lnk = new TEMP(lv.frame.FP());
		while(dest.parent != lv){
			lnk = lv.staticLink().access.exp(lnk);
			lv = lv.parent;
		}
		args = new ExpList(lnk, args);
		return new Ex(new CALL(new NAME(name), args));
	}
	
	Exp transExtCallExp(Level home, Label name, ArrayList<Exp> argValue){
		ExpList args = null;
		for(int i = argValue.size()-1; i>=0; --i)
			args = new ExpList(argValue.get(i).unEx(), args);
		return new Ex(home.frame.externalCall(name.toString(), args));
	}
	
	Exp combine2Stm(Exp e1, Exp e2){
		if(e1 == null)
			return new Nx(e2.unNx());
		else if (e2 == null)
			return new Nx(e1.unNx());
		else
			return new Nx(new SEQ(e1.unNx(), e2.unNx()));
	}
	
	Exp combine2Exp(Exp e1, Exp e2){
		if(e1 == null)
			return new Ex(e2.unEx());
		else
			return new Ex(new ESEQ(e1.unNx(), e2.unEx()));	
	}
	
	Exp transRecordExp(Level home, ArrayList<Exp> field){
		Temp addr = new Temp();
		int size = 0;
		if(field.size() != 0)
			size = field.size()*wordSize;
		Expr alloc = home.frame.externalCall("allocRecord", new ExpList(new CONST(size), null));
		//Stm init = transNoOp().unNx();//��ʼ��ָ��
		Stm init = new EXP(new CONST(0));//��ʼ��ָ��
		for(int i = field.size()-1; i>=0; --i){
			Expr offset = new BINOP(BINOP.PLUS, new TEMP(addr), new CONST(i*wordSize));
			Expr v = field.get(i).unEx();
			init = new SEQ(new MOVE(new MEM(offset),v), init);
		}
		return new Ex(new ESEQ(new SEQ(new MOVE(new TEMP(addr), alloc), init),new TEMP(addr)));
	}
	
	Exp transArrayExp(Level home, Exp init, Exp size){
		Expr alloc = home.frame.externalCall("initArray", new ExpList(size.unEx(), new ExpList(init.unEx(),null)));
		return new Ex(alloc);
	}
	
	Exp transIfExp(Exp test, Exp e1, Exp e2){
		return new IfExp(test, e1, e2);
	}
	
	Exp transWhileExp(Exp test, Exp body, Label done){
		return new WhileExp(test, body, done);
	}
	
	Exp transForExp(Level home, Access var, Exp low, Exp high, Exp body, Label done){
		return new ForExp(home, var, low, high, body, done);
	}
	
	Exp transBreakExp(Label peekLabel){
		return new Nx(new JUMP(peekLabel));
	}
	//ָ�����ϵ������ǽ���һ��Label�Ķ�ջ��Ȼ��ͨ��ѹ��Label�ͻ�ȡLabel�ķ���ȡ�����Label��

	/*
	 * �û���ѽ������������������ʲô��˼��
	 * ���ҵ���⣺���ֿ����ԣ�
	 * 1> home��current_lv, Ȼ��acc����ǰ������һ��������ͨ��staticlink����ǰ�����ı�����lv,
	 * �ҵ���ѵ�ǰ��ֵ������ǰ������var��Ӧ��λ��
	 * �����������ǣ����֪�����ҵ�acc������ǰ����ı�����acc
	 * 2> home����ǰ������lv��acc�ǵ�ǰ������acc��Ȼ����ݵ�ǰ��acc����ǰ��lv
	 * ��������������ô֪�������������ĸ�lv��
	 * 
	 * �Լ�Access, Expr, Level֮��Ĺ�ϵ���������
	 * result = lv.staticLink().access.exp(result);
	 * 
	 * �Լ�Exp��Expr�Ĺ�ϵ
	 */
	Exp transSimpleVar(Access acc, Level home){
		Expr result = new TEMP(home.frame.FP());
		Level lv = home;
		while(lv != acc.home){
			result = lv.staticLink().access.exp(result);
			lv = lv.parent;
		}
		return new Ex(acc.access.exp(result));
	}
	
	Exp transSubscriptVar(Exp var, Exp index){
		Expr array_addr = var.unEx();//var�������ΪҲ��������׵�ַ��
		Expr array_offset = new BINOP(BINOP.MUL, index.unEx(), new CONST(wordSize));
		return new Ex(new MEM(new BINOP(BINOP.PLUS, array_addr, array_offset)));
	}
	
	Exp transFieldVar(Exp var, int num){
		Expr record_addr = var.unEx();
		Expr record_offset = new CONST(num*wordSize);
		return new Ex(new MEM(new BINOP(BINOP.PLUS, record_addr, record_offset)));
	}
	
	public void procEntryExit(Level home, Exp body, boolean returnValue){
		Stm stm = null;
		if(returnValue)
			stm = new MOVE(new TEMP(home.frame.RV()),body.unEx());
		else
			stm = body.unNx();
		stm = home.frame.procEntryExit1(stm);
		addFrag(new ProcFrag(stm, home.frame));
	}
}

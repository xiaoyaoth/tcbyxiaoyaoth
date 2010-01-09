package Translate;

/*************************************
 * 完全不理解Access
 * 导致核Access相关的概念都不理解了
 * 比如静态链接。管Access屌事。
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
	
	//返回expr供semant打印
	public Tree.Expr exprResult(Exp e){
		return e.unEx();
	}
	
	//返回stm供semant打印
	public Tree.Stm stmResult(Exp e){
		return e.unNx();
	}
	
	//为了让类型检查和翻译中间树能兼容，返回一个没有值的东西，保证翻译中间树能继续。这样比直接返回null好。
	public Nx nothingResult(){
		return new Nx(null);
	}
	
	/*
	 * tool-function ends;
	 */
	/*
	 * trans begin
	 */
	public Exp transIntExp(int value){
		return new Ex(new CONST(value));
	}
	
	public Exp transStringExp(String value){
		Label l = new Label();
		addFrag (new DataFrag(l, frame.string(l, value)));
		return new Ex(new NAME(l));
	}
	
	public Exp transNilExp(){
		return new Ex(new CONST(0));
	}
	
	public Exp transVarExp(Exp e){
		return e;
	}
	
	public Exp transCalcExp(int oper, Exp left, Exp right){
		return new Ex(new BINOP(oper, left.unEx(), right.unEx()));
	}
	
	public Exp transStringRelExp(int oper, Exp left, Exp right){
		Expr compare  = home.frame.externalCall("stringEqual", new ExpList(left.unEx(), new ExpList(right.unEx(), null)));
		return new RelCx(oper, new Ex(compare), new Ex(new CONST(0)));
	}
	
	public Exp transOtherRelExp(int oper, Exp left, Exp right){
		return new RelCx(oper, left, right);
	}
	
	public Exp transAssignExp(Exp lvalue, Exp exp){
		if(lvalue!=null&&exp!=null)
			return new Nx(new MOVE(lvalue.unEx(), exp.unEx()));
		else
			return new Nx(null);
	}
	
	public Exp transCallExp(Level home, Level dest, Label name, ArrayList<Exp> argValue){
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
	
	public Exp transExtCallExp(Level home, Label name, ArrayList<Exp> argValue){
		ExpList args = null;
		for(int i = argValue.size()-1; i>=0; --i)
			args = new ExpList(argValue.get(i).unEx(), args);
		return new Ex(home.frame.externalCall(name.toString(), args));
	}
	
	public Exp combine2Stm(Exp e1, Exp e2){
		if(e1 == null){
			if(e2 == null)
				return null;
			return new Nx(e2.unNx());}
		else if (e2 == null)
			return new Nx(e1.unNx());
		else
			return new Nx(new SEQ(e1.unNx(), e2.unNx()));
	}
	
	public Exp combine2Exp(Exp e1, Exp e2){
		if(e2 == null)
			throw new Error("Combin2Exp:the second can't be null");
		if(e1 == null){
			return new Ex(e2.unEx());}
		else
			return new Ex(new ESEQ(e1.unNx(), e2.unEx()));	
	}
	
	public Exp transRecordExp(Level home, ArrayList<Exp> field){
		Temp addr = new Temp();
		int size = 0;
		if(field.size() != 0)
			size = field.size()*wordSize;
		Expr alloc = home.frame.externalCall("allocRecord", new ExpList(new CONST(size), null));
		//Stm init = transNoOp().unNx();//初始化指令
		Stm init = new EXP(new CONST(0));//初始化指令
		for(int i = field.size()-1; i>=0; --i){
			Expr offset = new BINOP(BINOP.PLUS, new TEMP(addr), new CONST(i*wordSize));
			Expr v = field.get(i).unEx();
			init = new SEQ(new MOVE(new MEM(offset),v), init);
		}
		return new Ex(new ESEQ(new SEQ(new MOVE(new TEMP(addr), alloc), init),new TEMP(addr)));
	}
	
	public Exp transArrayExp(Level home, Exp init, Exp size){
		Expr alloc = home.frame.externalCall("initArray", new ExpList(size.unEx(), new ExpList(init.unEx(),null)));
		return new Ex(alloc);
	}
	
	public Exp transIfExp(Exp test, Exp e1, Exp e2){
		return new IfExp(test, e1, e2);
	}
	
	public Exp transWhileExp(Exp test, Exp body, Label done){
		return new WhileExp(test, body, done);
	}
	
	public Exp transForExp(Level home, Access var, Exp low, Exp high, Exp body, Label done){
		return new ForExp(home, var, low, high, body, done);
	}
	
	public Exp transBreakExp(Label peekLabel){
		return new Nx(new JUMP(peekLabel));
	}
	//指导书上的做法是建立一个Label的堆栈，然后通过压入Label和获取Label的方法取得最顶的Label；

	/*
	 * 好混乱呀，这两个参数到底是什么意思？
	 * 按我的理解：两种可能性：
	 * 1> home是current_lv, 然后acc是以前声明的一个东西，通过staticlink找以前声明的变量的lv,
	 * 找到则把当前的值附到以前声明的var对应的位置
	 * 这样的问题是，如何知道我找的acc就是以前定义的变量的acc
	 * 2> home是以前声明的lv，acc是当前变量的acc，然后根据当前的acc找以前的lv
	 * 这样的问题是怎么知道变量定义在哪个lv。
	 * 
	 * 以及Access, Expr, Level之间的关系，比如这句
	 * result = lv.staticLink().access.exp(result);
	 * 
	 * 以及Exp和Expr的关系
	 */
	public Exp transSimpleVar(Access acc, Level home){
		Expr result = new TEMP(home.frame.FP());
		Level lv = home;
		while(lv != acc.home){
			result = lv.staticLink().access.exp(result);
			lv = lv.parent;
		}
		return new Ex(acc.access.exp(result));
	}
	
	public Exp transSubscriptVar(Exp var, Exp index){
		Expr array_addr = var.unEx();//var可以理解为也是数组的首地址；
		Expr array_offset = new BINOP(BINOP.MUL, index.unEx(), new CONST(wordSize));
		return new Ex(new MEM(new BINOP(BINOP.PLUS, array_addr, array_offset)));
	}
	
	public Exp transFieldVar(Exp var, int num){
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

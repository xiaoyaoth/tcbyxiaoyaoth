package Semant;

import Absyn.*;
import Translate.Level;
import Temp.Label;
import Types.INT;
import Types.NIL;
import Types.RECORD;
import Types.STRING;
import Types.Type;
import Types.VOID;

public class Semant {
	public boolean errorFlag = false;
	Env env;

	Level current_lv;// current_level
	Label current_lb;// current_label
	int loopMark;

	ErrorMsg.ErrorMsg err = env.error;

	public Semant(ErrorMsg.ErrorMsg err) {
		this(new Env(err, null));
		this.err = err;
	}

	Semant(Env e) {
		env = e;
	}

	Translate.Exp checkInt(ExpTy et, int pos) {
		if (!(et.ty.actual() instanceof Types.INT))
			err.error(pos, "integer required");
		return et.exp;
	}

	boolean equalTy(Types.Type l_ty, Types.Type r_ty) {
		boolean l_rcd = l_ty.actual() instanceof Types.RECORD;
		boolean l_nil = l_ty.actual() instanceof Types.NIL;
		boolean r_rcd = r_ty.actual() instanceof Types.RECORD;
		boolean r_nil = r_ty.actual() instanceof Types.NIL;
		boolean others = l_ty.actual().coerceTo(r_ty);
		return ((l_rcd && r_rcd) || (l_nil && r_nil) || (!l_nil && !r_nil || others));
	}

	boolean isLoopVar(SimpleVar v){
		Entry entry = (Entry)env.vEnv.get(v.name);
		if(entry instanceof LoopVarEntry)
			return true;
		else
			return false;
	}
	
	// expr type_checking
	ExpTy transExp(Exp e) {
		if (e instanceof ArrayExp)
			return transExp((ArrayExp) e);
		if (e instanceof AssignExp)
			return transExp((AssignExp) e);
		if (e instanceof BreakExp)
			return transExp((BreakExp) e);
		if (e instanceof CallExp)
			return transExp((CallExp) e);
		if (e instanceof ForExp)
			return transExp((ForExp) e);
		if (e instanceof IfExp)
			return transExp((IfExp) e);
		if (e instanceof IntExp)
			return transExp((IntExp) e);
		if (e instanceof LetExp)
			return transExp((LetExp) e);
		if (e instanceof NilExp)
			return transExp((NilExp) e);
		if (e instanceof OpExp)
			return transExp((OpExp) e);
		if (e instanceof RecordExp)
			return transExp((RecordExp) e);
		if (e instanceof SeqExp)
			return transExp((SeqExp) e);
		if (e instanceof StringExp)
			return transExp((StringExp) e);
		if (e instanceof VarExp)
			return transExp((VarExp) e);
		if (e instanceof WhileExp)
			return transExp((WhileExp) e);
		if (e == null)
			return new ExpTy(null, new Types.VOID());
		throw new Error("transExp");
	}

	// var type_checking
	ExpTy transVar(Var e) {
		if (e instanceof SimpleVar)
			return transVar((SimpleVar) e);
		if (e instanceof SubscriptVar)
			return transVar((SubscriptVar) e);
		if (e instanceof FieldVar)
			return transVar((FieldVar) e);
		throw new Error("transVar");
	}

	// dec type_checking
	Translate.Exp transDec(Dec e) {
		if (e instanceof VarDec)
			return transDec((VarDec) e);
		if (e instanceof TypeDec)
			return transDec((TypeDec) e);
		if (e instanceof FunctionDec)
			return transDec((FunctionDec) e);
		throw new Error("transDec");
	}

	// type type_checking
	Types.Type transTy(Ty e) {
		if (e instanceof NameTy)
			return transTy((NameTy) e);
		if (e instanceof ArrayTy)
			return transTy((ArrayTy) e);
		if (e instanceof RecordTy)
			return transTy((RecordTy) e);
		throw new Error("transTy");
	}

	/*
	 * type checking very specific expression;
	 */
	
	//IntExp
	ExpTy transExp(IntExp e){
		return new ExpTy(null, new Types.INT());
	}
	
	//StringExp
	ExpTy transExp(StringExp e){
		return new ExpTy(null, new Types.STRING());
	}
	
	//NilExp
	ExpTy transExp(NilExp e){
		return new ExpTy(null, new Types.NIL());
	}
	
	//VarExp
	ExpTy transExp(VarExp e){
		return transVar(e.var);
	}
	
	//AssignExp
	ExpTy transExp(AssignExp e){
		if(e.var instanceof SimpleVar && !isLoopVar((SimpleVar)e.var))
			err.error(e.var.pos, "AssignExp:not Simple");
		ExpTy r = (ExpTy)transExp(e.exp);
		if(r.ty instanceof VOID)
			err.error(e.exp.pos, "AssignExp:can't assign a void to variable");
		ExpTy l = (ExpTy)transVar(e.var);
		if(!r.ty.actual().coerceTo(l.ty))
			err.error(e.pos, "AssignExp:cannot assign a "+ r.ty.toString() + " to "+ l.ty.toString());
		return new ExpTy(null, new VOID());	
	}

	//CallExp
	ExpTy transExp(CallExp e){
		Entry func_entry = (Entry)env.vEnv.get(e.func);
		if(func_entry == null)
			err.error(e.pos, "CallExp:function is not declared");
		if(!(func_entry instanceof FuncEntry)&&!(func_entry instanceof StdFuncEntry)){
			err.error(e.pos, "CallExp:not find function with the id of " +e.func);
		}
		
		ExpList argr = e.args;//实参
		Types.RECORD argf = ((FuncEntry)func_entry).formals;//形参
		for(; argr!=null && argf!=null; argr = argr.tail , argf = argf.tail){
			ExpTy arg_result = transExp(argr.head);
			if(!arg_result.ty.actual().coerceTo(argf.fieldType))
				err.error(argr.head.pos, "CallExp:argument type is not matched");
			if(argr!=null)
				err.error(argr.head.pos, "CallExp:more arguments than declared");
			if(argf!=null)
				err.error(argr.head.pos, "CallExp:lesser arguments than declared");
		}
		Types.Type resultTy = ((FuncEntry)env.vEnv.get(e.func)).result;
		return new ExpTy(null, resultTy);
	}
	
	//ForExp
	ExpTy transExp(ForExp e){
		loopMark++;
		Label labelTmp = current_lb;
		current_lb = new Label();
		
		ExpTy low = transExp(e.var.init);
		ExpTy high = transExp(e.hi);
		
		checkInt(low, e.var.init.pos);
		checkInt(high, e.hi.pos);
		Translate.Access varAcc = current_lv.allocLocal(e.var.escape);
		
		env.vEnv.beginScope();
		env.vEnv.put(e.var.name, new LoopVarEntry(varAcc, new INT()));
		transExp(e.body);
		env.vEnv.endScope();
		
		current_lb = labelTmp;
		loopMark--;
		
		return new ExpTy(null, new VOID());
	}
	
	//OpExp
	ExpTy transExp(OpExp e) {
		ExpTy l = (ExpTy) transExp(e.left);
		ExpTy r = (ExpTy) transExp(e.right);

		switch (e.oper) {
		case OpExp.PLUS:
		case OpExp.MINUS:
		case OpExp.MUL:
		case OpExp.DIV:
			checkInt(l, e.left.pos);
			checkInt(r, e.left.pos);
			break;
		case OpExp.EQ:
		case OpExp.NE:
			if (l.ty.actual() instanceof Types.VOID)
				err.error(e.left.pos, "OpExp.left can't be void");
			if (r.ty.actual() instanceof Types.VOID)
				err.error(e.right.pos, "OpExp.right can't be void");
			if (l.ty.actual() instanceof Types.NIL
					&& r.ty.actual() instanceof Types.NIL)
				err.error(e.pos, "OpExp LR can't all be NIL");
			if (equalTy(l.ty, r.ty))
				err.error(e.pos, "OpExp LR are not equal type");
			break;
		case OpExp.LT:
		case OpExp.LE:
		case OpExp.GT:
		case OpExp.GE:
			if (!(l.ty.actual() instanceof Types.INT && r.ty.actual() instanceof Types.INT)
					|| !(l.ty.actual() instanceof Types.STRING && r.ty.actual() instanceof Types.STRING))
				err.error(e.pos, "OpExp LR must be equally int or string");
			break;
		}
		return new ExpTy(null, new Types.INT());
	}

	//RecordExp
	ExpTy transExp(RecordExp e){
		Type ty = (Type)env.tEnv.get(e.typ);
		if(ty == null||!(ty instanceof RECORD))
			env.error.error(e.pos, "RecordExp: wrong record type");
		FieldExpList fieldexpr = e.fields;
		Types.RECORD fieldty = (RECORD) ((RECORD)ty).fieldType;
		for(; fieldexpr!=null && fieldty!=null; fieldexpr=fieldexpr.tail, fieldty=fieldty.tail){
			if(!fieldexpr.name.equals(fieldty.fieldName))
				env.error.error(fieldexpr.pos, "RecordExp:field name is not matched");
			
		}
		return null;
	}
}

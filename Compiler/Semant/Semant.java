package Semant;

import java.util.HashSet;

import Absyn.*;
import Translate.Level;
import Temp.Label;
import Types.INT;
import Types.NIL;
import Types.RECORD;
import Types.STRING;
import Types.Type;
import Types.VOID;
import Types.ARRAY;
import Types.NAME;
import Util.BoolList;

public class Semant {
	public boolean errorFlag = false;
	Env env;

	Level current_lv;// current_level
	Label current_lb;// current_label
	int loopMark;

	//ErrorMsg.ErrorMsg err = env.error;

	public Semant(ErrorMsg.ErrorMsg err) {
		this(new Env(err, null));
	}

	Semant(Env e) {
		env = e;
	}

	/*
	 * tool functions
	 */
	Translate.Exp checkInt(ExpTy et, int pos) {
		if (!(et.ty.actual() instanceof INT))
			env.error.error(pos, "integer required");
		return et.exp;
	}

	boolean equalTy(Type l_ty, Type r_ty) {
		boolean l_rcd = l_ty.actual() instanceof RECORD;
		boolean l_nil = l_ty.actual() instanceof NIL;
		boolean r_rcd = r_ty.actual() instanceof RECORD;
		boolean r_nil = r_ty.actual() instanceof NIL;
		boolean others = l_ty.actual().coerceTo(r_ty);
		return ((l_rcd && r_rcd) || (l_nil && r_nil) || (!l_nil && !r_nil || others));
	}

	boolean isLoopVar(SimpleVar v) {
		Entry entry = (Entry) env.vEnv.get(v.name);
		if (entry instanceof LoopVarEntry)
			return true;
		else
			return false;
	}

	BoolList makeBoolList(FieldList flist){
		if (flist == null)
			return null;
		else
			return new BoolList(flist.escape, makeBoolList(flist.tail));
	}
	
	/*
	 * tool function ends
	 */
	/*
	 * type_checking interface
	 */
	
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
			return new ExpTy(null, new VOID());
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
	Type transTy(Ty e) {
		if (e instanceof NameTy)
			return transTy((NameTy) e);
		if (e instanceof ArrayTy)
			return transTy((ArrayTy) e);
		if (e instanceof RecordTy)
			return transTy((RecordTy) e);
		throw new Error("transTy");
	}

	/*
	 * type checking interface ends
	 */	
	/*
	 * type checking very specific expression;
	 */

	// IntExp
	ExpTy transExp(IntExp e) {
		return new ExpTy(null, new INT());
	}

	// StringExp
	ExpTy transExp(StringExp e) {
		return new ExpTy(null, new STRING());
	}

	// NilExp
	ExpTy transExp(NilExp e) {
		return new ExpTy(null, new NIL());
	}

	// VarExp
	ExpTy transExp(VarExp e) {
		return transVar(e.var);
	}

	// ArrayExp
	ExpTy transExp(ArrayExp e) {
		Type ty = (Type) env.tEnv.get(e.typ);
		if (ty == null || !(ty instanceof ARRAY))
			env.error.error(e.pos, "ArrayExp:array not defined");

		ExpTy array_range = (ExpTy) transExp(e.size);
		checkInt(array_range, e.size.pos);

		ARRAY array_ty = (ARRAY) ty.actual();
		ExpTy expr_ty = transExp(e.init);
		if (array_ty.element.actual().coerceTo(expr_ty.ty))
			env.error.error(e.pos, "ArrayExp:not the same type as declared");
		return new ExpTy(null, array_ty);
	}

	// AssignExp
	ExpTy transExp(AssignExp e) {
		if (e.var instanceof SimpleVar && !isLoopVar((SimpleVar) e.var))
			env.error.error(e.var.pos, "AssignExp:not Simple variable");
		ExpTy r = (ExpTy) transExp(e.exp);
		if (r.ty instanceof VOID)
			env.error.error(e.exp.pos, "AssignExp:can't assign a void to variable");
		ExpTy l = (ExpTy) transVar(e.var);
		if (!r.ty.actual().coerceTo(l.ty))
			env.error.error(e.pos, "AssignExp:cannot assign a " + r.ty.toString()
					+ " to " + l.ty.toString());
		return new ExpTy(null, new VOID());
	}

	// BreakExp
	ExpTy transExp(BreakExp e) {
		if (loopMark == 0)
			env.error.error(e.pos, "BreakExp:there are no loops, cannot break");
		return new ExpTy(null, new VOID());
	}

	// CallExp
	ExpTy transExp(CallExp e) {
		Entry func_entry = (Entry) env.vEnv.get(e.func);
		if (func_entry == null)
			env.error.error(e.pos, "CallExp:function is not declared");
		if (!(func_entry instanceof FuncEntry)
				&& !(func_entry instanceof StdFuncEntry)) {
			env.error.error(e.pos, "CallExp:not find function with the id of "
					+ e.func);
		}

		ExpList argr = e.args;// 实参
		RECORD argf = ((FuncEntry) func_entry).formals;// 形参
		for (; argr != null && argf != null; argr = argr.tail, argf = argf.tail) {
			ExpTy arg_result = transExp(argr.head);
			if (!arg_result.ty.actual().coerceTo(argf.fieldType))
				env.error.error(argr.head.pos,"CallExp:argument type is not matched");
		}

		if (argr != null)
			env.error.error(argr.head.pos, "CallExp:more arguments than declared");
		if (argf != null)
			env.error.error(argr.head.pos, "CallExp:lesser arguments than declared");

		Type resultTy = ((FuncEntry) env.vEnv.get(e.func)).result;
		return new ExpTy(null, resultTy);
	}

	// ForExp
	ExpTy transExp(ForExp e) {
		loopMark++;
		// Label labelTmp = current_lb;
		// current_lb = new Label();

		ExpTy low = transExp(e.var.init);
		ExpTy high = transExp(e.hi);

		checkInt(low, e.var.init.pos);
		checkInt(high, e.hi.pos);
		Translate.Access varAcc = current_lv.allocLocal(e.var.escape);

		env.vEnv.beginScope();
		env.vEnv.put(e.var.name, new LoopVarEntry(varAcc, new INT()));
		transExp(e.body);
		env.vEnv.endScope();

		// current_lb = labelTmp;
		loopMark--;

		return new ExpTy(null, new VOID());
	}

	// IfExp
	ExpTy transExp(IfExp e) {
		ExpTy test_ty = transExp(e.test);
		ExpTy then_ty = transExp(e.thenclause);
		ExpTy else_ty = transExp(e.elseclause);

		checkInt(test_ty, e.test.pos);
		if (e.elseclause != null) {
			if (!(then_ty.ty.actual() instanceof VOID))
				env.error.error(e.pos, "then-clause should not return value");
		} else if (!equalTy(then_ty.ty, else_ty.ty))
			env.error.error(e.pos,"values returned from different condition is not matched");
		return new ExpTy(null, then_ty.ty.actual());
	}

	// LetExp
	ExpTy transExp(LetExp e) {
		env.vEnv.beginScope();
		env.tEnv.beginScope();

		for (DecList dec_list = e.decs; dec_list != null; dec_list = e.decs.tail)
			transDec(dec_list.head);

		ExpTy let_body = transExp(e.body);

		env.vEnv.endScope();
		env.tEnv.endScope();

		return new ExpTy(null, let_body.ty.actual());
	}

	// OpExp
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
			if (l.ty.actual() instanceof VOID)
				env.error.error(e.left.pos, "OpExp.left can't be void");
			if (r.ty.actual() instanceof VOID)
				env.error.error(e.right.pos, "OpExp.right can't be void");
			if (l.ty.actual() instanceof NIL && r.ty.actual() instanceof NIL)
				env.error.error(e.pos, "OpExp LR can't all be NIL");
			if (equalTy(l.ty, r.ty))
				env.error.error(e.pos, "OpExp LR are not equal type");
			break;
		case OpExp.LT:
		case OpExp.LE:
		case OpExp.GT:
		case OpExp.GE:
			if (!(l.ty.actual() instanceof INT && r.ty.actual() instanceof INT)
					|| !(l.ty.actual() instanceof STRING && r.ty.actual() instanceof STRING))
				env.error.error(e.pos, "OpExp LR must be equally int or string");
			break;
		}
		return new ExpTy(null, new INT());
	}

	// RecordExp
	ExpTy transExp(RecordExp e) {
		Type ty = (Type) env.tEnv.get(e.typ);
		if (ty == null || !(ty instanceof RECORD))
			env.error.error(e.pos, "RecordExp: wrong record type");

		FieldExpList fieldexpr = e.fields;
		RECORD fieldty = (RECORD) ty.actual(), result = fieldty;
		for (; fieldexpr != null && fieldty != null; fieldexpr = fieldexpr.tail, fieldty = fieldty.tail) {
			if (!fieldexpr.name.equals(fieldty.fieldName))
				env.error.error(fieldexpr.pos,"RecordExp:field name is not matched");
			ExpTy fexpr_ty = (ExpTy) transExp(fieldexpr.init);
			if (!equalTy(fexpr_ty.ty, fieldty.fieldType))
				env.error.error(fieldexpr.pos,"RecordExp:this field's type doesn't match what declared");
		}

		if (fieldexpr != null)
			env.error.error(fieldexpr.pos, "more fields than declared");
		if (fieldty != null)
			env.error.error(e.pos, "less fields than declared");

		return new ExpTy(null, result);
	}

	// WhileExp
	ExpTy transExp(WhileExp e) {
		loopMark++;

		ExpTy test_ty = transExp(e.test);
		ExpTy body_ty = transExp(e.body);

		checkInt(test_ty, e.test.pos);
		if (!(body_ty.ty instanceof VOID))
			env.error.error(e.body.pos, "WhileExp:cannot return a value");

		loopMark--;
		return new ExpTy(null, new VOID());
	}

	// SeqExp
	ExpTy transExp(SeqExp e) {
		ExpList elist = e.list;
		ExpTy result = null;
		for (; elist != null; elist = e.list.tail)
			result = transExp(elist.head);
		return new ExpTy(null, result.ty);
	}

	/*
	 * type checking expr ends;
	 */
	/*
	 * type checking very specific variable;
	 */

	// SimpleVar
	ExpTy transVar(SimpleVar v) {
		Entry var_entry = (Entry) env.vEnv.get(v.name);
		if (var_entry == null || !(var_entry instanceof VarEntry)) {
			env.error.error(v.pos,
					"SimpleVar: variable of this type is not defined");
		}
		return new ExpTy(null, ((VarEntry) var_entry).ty.actual());
	}

	// SubscriptVar
	ExpTy transVar(SubscriptVar v) {
		ExpTy var_ty = transVar(v.var);
		ExpTy index_ty = transExp(v.index);
		if (!(var_ty.ty instanceof ARRAY))
			env.error.error(v.pos, "SubscriptVar: not array type");
		checkInt(index_ty, v.index.pos);
		return new ExpTy(null, ((ARRAY) var_ty.ty.actual()).element);
		// array defined the types of the element as a member ARRAY class
	}

	// FieldVar
	ExpTy transVar(FieldVar v) {
		Type ty = new INT();
		ExpTy var_ty = transVar(v.var);

		if (!(var_ty.ty.actual() instanceof RECORD))
			env.error.error(v.var.pos, "FieldVar:RECORD type required");
		else {
			RECORD v_ty = (RECORD) var_ty.ty.actual();
			boolean found;
			for (found = false; v_ty != null; v_ty = v_ty.tail) {
				if (v_ty.fieldName == v.field) {
					found = true;
					ty = v_ty.fieldType;
					break;
				}
			}
			if (!found)
				env.error.error(v.pos,
						"FieldVar: not found any field match the reference");
		}
		return new ExpTy(null, ty);
	}

	/*
	 * type checking very specific type;
	 */

	// NameTy
	Type transTy(NameTy t) {
		Type ty = (Type) env.tEnv.get(t.name);
		if (ty == null)
			env.error.error(t.pos, "NameTy: type not defined");
		return ty.actual();
	}

	// ArrayTy
	Type transTy(ArrayTy t) {
		Type ty = (Type) env.tEnv.get(t.typ);
		if (ty == null)
			env.error.error(t.pos, "ArrayTy: type not defined");
		return new ARRAY(ty);
	}

	// RecordTy
	Type transTy(RecordTy t) {
		RECORD result = new RECORD(null, new VOID(), null);
		if (t.fields == null)
			return result;
		else {
			FieldList flist = t.fields;
			result = new RECORD(null, null, null);
			for (; flist != null; flist = flist.tail) {
				Type ty = (Type) env.tEnv.get(flist.name);
				if (ty == null) {
					env.error.error(flist.pos, "type not defined");
					break;
				} else {
					if (result.fieldName == null)
						result = new RECORD(flist.name, ty, null);
					else
						result.tail = new RECORD(flist.name, ty, null);
				}
			}
			return result;
		}
	}

	/*
	 * type checking var ends;
	 */
	/*
	 * type checking very specific declaration;
	 */

	// VarDec
	Translate.Exp transDec(VarDec d){
		if(null == d.init)
			env.error.error(d.pos, "VarDec: variable not initialized");
		else{
			Type typ_ty = new INT();
			ExpTy init_ty = transExp(d.init);
			if(d.typ == null ){
				if(init_ty.ty.actual() instanceof NIL)
					env.error.error(d.pos, "cannot initialize a variable with NIL");
				else
					typ_ty = init_ty.ty.actual();
			}else{
			typ_ty = transTy(d.typ);
			if(!typ_ty.actual().coerceTo(init_ty.ty))
				env.error.error(d.pos,"VarDec: type not match");
			}
			Translate.Access varAcc = current_lv.allocLocal(d.escape);
			env.vEnv.put(d.name, new VarEntry(varAcc, typ_ty));
			}
		return null;
	}

	//TypeDec
	Translate.Exp transDec(TypeDec d){
		HashSet<Symbol.Symbol> set = new HashSet<Symbol.Symbol>();
		TypeDec temp = d;
		
		for(; temp!=null; temp=temp.next){
			if(set.contains(temp.name))
				env.error.error(temp.pos, "TypeDec: type redefined");
			set.add(temp.name);
			env.tEnv.put(temp.name, new NAME(temp.name));
		}
		
		for(temp = d; temp != null; temp = temp.next)
			((NAME)env.tEnv.get(temp.name)).bind(transTy(temp.ty));
		
		for(temp = d; temp != null; temp = temp.next){
			NAME name_entry = (NAME)env.tEnv.get(d.name);
			if(name_entry.isLoop()){
				env.error.error(temp.pos, "TypeDec: cylic declaration found");
				name_entry.bind(new INT());
			}
		}
		return null;
	}
	
	//FunctionDec
	Translate.Exp transDec(FunctionDec d){
		HashSet<Symbol.Symbol> set = new HashSet<Symbol.Symbol>();
		FunctionDec temp = d;
		Level lv_temp = current_lv;
		
		for(; d != null; d = d.next){
			if(set.contains(d.name))
				env.error.error(d.pos, "FunctionDec: function name redefined");
			set.add(d.name);
			
			RECORD para_ty = (RECORD)transTy(new RecordTy(d.pos, d.params));
			
			Type result_ty = new VOID();
			if(d.result != null)
				result_ty = transTy(d.result);
			
			current_lv = new Level(lv_temp, d.name, makeBoolList(d.params));
			Label entry_label = new Label(d.name.toString());
			env.vEnv.put(d.name, new FuncEntry(para_ty, result_ty, entry_label, current_lv));
		}
		
		current_lv = lv_temp;
		
		for(d = temp; d!=null; d = d.next){
			env.vEnv.beginScope();
			int loopMark_temp = loopMark;
			loopMark = 0;
			
			if(env.vEnv.get(d.name)instanceof FuncEntry){
				Level lv_temp2 = current_lv;
				current_lv = ((FuncEntry)env.vEnv.get(d.name)).level;
				Translate.AccessList fmls = current_lv.formals.tail;
				
				for(FieldList flist = d.params; flist!=null; flist = flist.tail, fmls = fmls.tail){
					Type ty = (Type)env.tEnv.get(flist.typ);
					if(ty == null)
						env.error.error(flist.pos, "type undefined");
					else
						env.vEnv.put(flist.name, new VarEntry(new Translate.Access(current_lv, fmls.head.access), ty));
					
					ExpTy body_ty = transExp(d.body);
					if(!body_ty.ty.actual().coerceTo(((FuncEntry)env.vEnv.get(d.name)).result))
						env.error.error(d.pos, "type not matched");
					current_lv = lv_temp2;
				}
				loopMark = loopMark_temp;
				env.vEnv.endScope();
			}
		}
		return null;
	}

	/*
	 * type_checking declaration ends;
	 */
}

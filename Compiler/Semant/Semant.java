package Semant;

import java.util.ArrayList;
import java.util.HashSet;

import Absyn.*;
import Mips.MipsFrame;
import Temp.Label;
import Translate.Level;
import Types.*;
import Util.BoolList;

public class Semant {
	public boolean errorFlag = false;
	Env env;
	int loopMark;

	Level current_lv;
	Label current_lb;
	Translate.Translate trans = new Translate.Translate();

	public Semant(ErrorMsg.ErrorMsg err) {
		this(new Env(err, new Level(new MipsFrame(new Label("main")))));
	}

	Semant(Env e) {
		env = e;
		current_lv = e.root;
		current_lb = e.root.frame.name;
	}

	/*
	 * tool functions
	 */
	Translate.Exp checkInt(ExpTy et, int pos) {
		if (!(et.ty.actual() instanceof INT)
				&& !(et.ty.actual() instanceof NONE)) {
			env.error.error(pos, "integer required");
		}
		return et.exp;
	}

	boolean equalTy(Type l_ty, Type r_ty) {
		boolean l_rcd = l_ty.actual() instanceof RECORD;
		boolean l_nil = l_ty.actual() instanceof NIL;
		boolean r_rcd = r_ty.actual() instanceof RECORD;
		boolean r_nil = r_ty.actual() instanceof NIL;
		boolean others = l_ty.actual().coerceTo(r_ty);
		return ((l_rcd && r_nil) || (l_nil && r_rcd) || (!l_nil && !r_nil && others));
	}

	boolean isLoopVar(SimpleVar v) {
		Entry entry = (Entry) env.vEnv.get(v.name);
		if (entry instanceof LoopVarEntry)
			return true;
		else
			return false;
	}

	BoolList makeBoolList(FieldList flist) {
		if (flist == null)
			return null;
		else
			return new BoolList(flist.escape, makeBoolList(flist.tail));
	}

	public void transProg(Exp exp) {
		ExpTy result = transExp(exp);
		errorFlag = env.error.anyErrors;
		if(errorFlag)
			return;
		//System.out.println(result.exp.getClass());
		boolean returnValue = !(result.ty.actual() instanceof Types.VOID || result.ty == null);
		trans.procEntryExit(current_lv, result.exp, returnValue);
		
		Translate.Frag frags = trans.getResult();
		Tree.Print printer = new Tree.Print(System.out);
		System.out.println("******* IR tree *******");
		printer.prStm(trans.stmResult((Translate.ProcFrag)frags));
		System.out.println("********** end **********");
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
			return new ExpTy(trans.nothingResult(), new VOID());
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
		return new ExpTy(trans.transIntExp(e.value), new INT());
	}

	// StringExp
	ExpTy transExp(StringExp e) {
		return new ExpTy(trans.transStringExp(e.value), new STRING());
	}

	// NilExp
	ExpTy transExp(NilExp e) {
		return new ExpTy(trans.transNilExp(), new NIL());
	}

	// VarExp
	ExpTy transExp(VarExp e) {
		return transVar(e.var);
	}

	// ArrayExp
	ExpTy transExp(ArrayExp e) {
		Type ty = (Type) env.tEnv.get(e.typ);
		if (ty == null || !(ty.actual() instanceof ARRAY))
			env.error.error(e.pos, "ArrayExp:array not defined");

		ExpTy array_range = (ExpTy) transExp(e.size);
		checkInt(array_range, e.size.pos);

		ARRAY array_ty = (ARRAY) ty.actual();
		ExpTy expr_ty = transExp(e.init);
		if (!array_ty.element.actual().coerceTo(expr_ty.ty.actual()))
			env.error.error(e.pos, "ArrayExp:not the same type as declared");
		return new ExpTy(trans.transArrayExp(current_lv, array_range.exp,
				expr_ty.exp), array_ty);
	}

	// AssignExp
	ExpTy transExp(AssignExp e) {
		if (e.var instanceof SimpleVar && isLoopVar((SimpleVar) e.var))
			env.error.error(e.var.pos, "AssignExp:loop variable");
		ExpTy r = (ExpTy) transExp(e.exp);
		if (r.ty instanceof VOID)
			env.error.error(e.exp.pos,
					"AssignExp:can't assign a void to variable");
		ExpTy l = (ExpTy) transVar(e.var);
		if (!r.ty.actual().coerceTo(l.ty) && !(l.ty.actual() instanceof NONE)
				&& !(r.ty.actual() instanceof NONE))
			env.error.error(e.pos, "AssignExp:cannot assign a "
					+ r.ty.toString() + " to " + l.ty.toString());
		return new ExpTy(trans.transAssignExp(l.exp, r.exp), new VOID());
	}

	// BreakExp
	ExpTy transExp(BreakExp e) {
		if (loopMark == 0)
			env.error.error(e.pos, "BreakExp:there are no loops, cannot break");
		return new ExpTy(trans.transBreakExp(current_lb), new VOID());
	}

	// CallExp
	ExpTy transExp(CallExp e) {
		Entry func_entry = (Entry) env.vEnv.get(e.func);
		if (!(func_entry instanceof FuncEntry || func_entry instanceof StdFuncEntry)){
			env.error.error(e.pos, "CallExp:function [" + e.func
					+ "] not defined");
			return new ExpTy(trans.nothingResult(), new NONE());}

		ExpList argr = e.args;// 实参
		RECORD argf = ((FuncEntry) func_entry).formals;// 形参

		ArrayList<Translate.Exp> args = new ArrayList<Translate.Exp>();

		for (; argr != null && argf != null; argr = argr.tail, argf = argf.tail) {
			ExpTy arg_result = transExp(argr.head);
			if (!arg_result.ty.actual().coerceTo(argf.fieldType)
					&& !(arg_result.ty.actual() instanceof NONE))
				env.error.error(argr.head.pos, "CallExp:argument type ["
						+ arg_result.ty.actual()
						+ "] does not match request type ["
						+ argf.fieldType.getClass() + "]");
			args.add(arg_result.exp);
		}

		if (argr != null)
			env.error.error(argr.head.pos,
					"CallExp:more arguments than declared");
		if (argf != null) {
			env.error.error(e.pos, "CallExp:lesser arguments than declared");
		}

		Type resultTy = ((FuncEntry) env.vEnv.get(e.func)).result;
		Translate.Exp irexp = trans.transCallExp(current_lv,
				((FuncEntry) func_entry).level, ((FuncEntry) func_entry).label,
				args);

		if (func_entry instanceof StdFuncEntry) {
			resultTy = ((StdFuncEntry) env.vEnv.get(e.func)).result;
			irexp = trans.transExtCallExp(current_lv,
					((StdFuncEntry) func_entry).label, args);
		}

		return new ExpTy(irexp, resultTy);
	}

	// ForExp
	ExpTy transExp(ForExp e) {
		System.out.println("current label:"+current_lb);
		loopMark++;
		Label temp_lb = current_lb;
		current_lb = new Label();

		ExpTy low = transExp(e.var.init);
		ExpTy high = transExp(e.hi);

		checkInt(low, e.var.init.pos);
		checkInt(high, e.hi.pos);
		Translate.Access acc = current_lv.allocLocal(e.var.escape);
		env.vEnv.beginScope();
		env.vEnv.put(e.var.name, new LoopVarEntry(acc, new INT()));
		ExpTy body = transExp(e.body);
		env.vEnv.endScope();

		Translate.Exp irexp = trans.transForExp(current_lv, acc, low.exp,
				high.exp, body.exp, current_lb);

		loopMark--;
		current_lb = temp_lb;

		return new ExpTy(irexp, new VOID());
	}

	// IfExp
	ExpTy transExp(IfExp e) {
		ExpTy test_ty = transExp(e.test);
		ExpTy then_ty = transExp(e.thenclause);
		ExpTy else_ty = transExp(e.elseclause);

		checkInt(test_ty, e.test.pos);
		if (e.elseclause == null) {
			if (!(then_ty.ty.actual() instanceof VOID))
				env.error.error(e.pos,
						"IfExp: then-clause should not return value");
		} else if (!equalTy(then_ty.ty.actual(), else_ty.ty.actual())) {
			env.error
					.error(e.pos,
							"IfExp :values returned from different condition is not matched");
		}

		Translate.Exp irexp = trans.transIfExp(test_ty.exp, then_ty.exp,
				else_ty.exp);
		return new ExpTy(irexp, then_ty.ty.actual());
	}

	// LetExp
	ExpTy transExp(LetExp e) {
		env.vEnv.beginScope();
		env.tEnv.beginScope();
		Translate.Exp irexp = null;

		for (DecList dec_list = e.decs; dec_list != null; dec_list = dec_list.tail) {
			Translate.Exp dec_exp = transDec(dec_list.head);
			irexp = trans.combine2Stm(irexp, dec_exp);
		}

		ExpTy let_body = transExp(e.body);

		if (let_body == null || let_body.ty.actual() instanceof VOID)
			irexp = trans.combine2Stm(irexp, let_body.exp);
		else
			irexp = trans.combine2Exp(irexp, let_body.exp);

		env.vEnv.endScope();
		env.tEnv.endScope();

		return new ExpTy(irexp, let_body.ty.actual());
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
			if (!equalTy(l.ty, r.ty))
				env.error.error(e.pos, "OpExp LR are not equal type");
			break;
		case OpExp.LT:
		case OpExp.LE:
		case OpExp.GT:
		case OpExp.GE:
			if (!(l.ty.actual() instanceof INT && r.ty.actual() instanceof INT)
					&& !(l.ty.actual() instanceof STRING && r.ty.actual() instanceof STRING)) {
				env.error
						.error(e.pos, "OpExp LR must be equally int or string");
			}
			break;
		}
		Translate.Exp irexp = trans.transCalcExp(e.oper, l.exp, r.exp);
		return new ExpTy(irexp, new INT());
	}

	// RecordExp
	ExpTy transExp(RecordExp e) {
		Type ty = (Type) env.tEnv.get(e.typ);
		if (ty == null || !(ty.actual() instanceof RECORD)) {
			env.error.error(e.pos, "RecordExp: [" + e.typ
					+ "] wrong record type");
			return new ExpTy(trans.nothingResult(), new NONE());
		} else {
			ArrayList<Translate.Exp> field_irexps = new ArrayList<Translate.Exp>();
			FieldExpList fieldexpr = e.fields;
			RECORD fieldty = (RECORD) ty.actual(), result = fieldty;
			for (; fieldexpr != null && fieldty != null; fieldexpr = fieldexpr.tail, fieldty = fieldty.tail) {
				if (!fieldexpr.name.equals(fieldty.fieldName)) {
					env.error.error(fieldexpr.pos, "RecordExp: ["
							+ fieldexpr.name + "] field name is not matched");
				}
				ExpTy fexpr_ty = (ExpTy) transExp(fieldexpr.init);
				if (!equalTy(fexpr_ty.ty, fieldty.fieldType))
					env.error
							.error(fieldexpr.pos,
									"RecordExp: this field's type doesn't match what declared");
				field_irexps.add(fexpr_ty.exp);
			}
			if (fieldexpr != null)
				env.error.error(fieldexpr.pos,
						"RecordExp: more fields than declared");
			if (fieldty != null)
				env.error.error(e.pos, "RecordExp: less fields than declared");

			Translate.Exp irexp = trans
					.transRecordExp(current_lv, field_irexps);
			return new ExpTy(irexp, result);
		}
	}

	// WhileExp
	ExpTy transExp(WhileExp e) {
		System.out.println("current label:"+current_lb);
		loopMark++;
		Label temp_lb = current_lb;
		current_lb = new Label();

		ExpTy test_ty = transExp(e.test);
		ExpTy body_ty = transExp(e.body);

		checkInt(test_ty, e.test.pos);
		if (!(body_ty.ty instanceof VOID))
			env.error.error(e.body.pos, "WhileExp:cannot return a value");

		Translate.Exp irexp = trans.transWhileExp(test_ty.exp, body_ty.exp,
				current_lb);

		loopMark--;
		current_lb = temp_lb;
		return new ExpTy(irexp, new VOID());
	}

	// SeqExp
	ExpTy transExp(SeqExp e) {
		ExpList elist = e.list;
		ExpTy seg = null;
		Translate.Exp irexp = null;
		if (elist == null)
			return new ExpTy(null, new VOID());
		for (; elist.tail != null; elist = elist.tail) {
			seg = transExp(elist.head);
			irexp = trans.combine2Stm(irexp, seg.exp);
		}
		seg = transExp(elist.head);
		if(seg.ty.actual() instanceof VOID)
			irexp = trans.combine2Stm(irexp, seg.exp);
		else
			irexp = trans.combine2Exp(irexp, seg.exp);
		
		return new ExpTy(irexp, seg.ty);
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
			env.error.error(v.pos, "SimpleVar: variable [" + v.name
					+ "] is not defined");
			return new ExpTy(trans.nothingResult(), new NONE());
		}
		Translate.Exp irexp = trans.transSimpleVar(((VarEntry)var_entry).acc, current_lv);
		return new ExpTy(irexp, ((VarEntry) var_entry).ty.actual());
	}

	// SubscriptVar
	ExpTy transVar(SubscriptVar v) {
		ExpTy var_ty = transVar(v.var);
		if (!(var_ty.ty.actual() instanceof ARRAY)) {
			env.error.error(v.pos, "SubscriptVar: not array type");
			return new ExpTy(trans.nothingResult(), new Types.NONE());
		} else {
			ExpTy index_ty = transExp(v.index);
			checkInt(index_ty, v.index.pos);
			Translate.Exp irexp = trans.transSubscriptVar(var_ty.exp, index_ty.exp);
			return new ExpTy(irexp, ((ARRAY) var_ty.ty.actual()).element);
		}
		// array defines the types of the element as a member ARRAY class
	}

	// FieldVar
	ExpTy transVar(FieldVar v) {
		Type ty = new NONE();
		ExpTy var_ty = transVar(v.var);
		int count = 0;

		if (!(var_ty.ty.actual() instanceof RECORD))
			env.error.error(v.var.pos, "FieldVar:RECORD type required");
		else {
			RECORD v_ty = (RECORD) var_ty.ty.actual();
			boolean found = false;
			for (; v_ty != null; v_ty = v_ty.tail, count++) {
				if (v_ty.fieldName == v.field) {
					found = true;
					ty = v_ty.fieldType;
					break;
				}
			}
			if (!found)
				env.error.error(v.pos,
						"FieldVar: not found any field match the reference ["
								+ v.field + "]");
		}
		Translate.Exp irexp = trans.transFieldVar(var_ty.exp, count);
		return new ExpTy(irexp, ty);
	}

	/*
	 * type checking very specific type;
	 */

	// NameTy
	Type transTy(NameTy t) {
		Type ty = (Type) env.tEnv.get(t.name);
		if (ty == null) {
			env.error.error(t.pos, "NameTy: type not defined");
			return new NONE();
		}
		return ty;
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
		RECORD result = null, temp = result;
		if (t.fields == null)
			return result;
		else {
			FieldList flist = t.fields;
			Type ty = (Type) env.tEnv.get(flist.typ);
			if (ty == null) {
				env.error.error(flist.pos, "RecordTy: [" + flist.typ
						+ "] type not defined");
			} else {
				result = new RECORD(flist.name, ty, null);
				temp = result;
				for (flist = t.fields.tail; flist != null; flist = flist.tail) {
					ty = (Type) env.tEnv.get(flist.typ);
					if (ty == null) {
						env.error.error(flist.pos, "RecordTy: [" + flist.typ
								+ "] type not defined");
						break;
					} else {
						temp.tail = new RECORD(flist.name, ty, null);
						temp = temp.tail;
					}
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
	Translate.Exp transDec(VarDec d) {
		Translate.Access acc = null;
		ExpTy init_ty = null;
		if (null == d.init)
			env.error.error(d.pos, "VarDec: variable not initialized");
		else {
			Type typ_ty = new NONE();
			init_ty = transExp(d.init);
			if (d.typ == null) {
				if (init_ty.ty.actual() instanceof NIL)
					env.error.error(d.pos,
							"cannot initialize a variable with NIL");
				else
					typ_ty = init_ty.ty.actual();
			} else {
				typ_ty = transTy(d.typ);
				if (!equalTy(typ_ty.actual(), init_ty.ty.actual())) {
					env.error.error(d.pos, "VarDec: type not match");
				}
			}
			acc = current_lv.allocLocal(d.escape);
			env.vEnv.put(d.name, new VarEntry(acc, typ_ty));
		}
		
		Translate.Exp irexp1 = trans.transSimpleVar(acc, current_lv);
		Translate.Exp irexp2 = trans.transAssignExp(irexp1, init_ty.exp);
		return irexp2;
	}

	// TypeDec
	Translate.Exp transDec(TypeDec d) {
		HashSet<Symbol.Symbol> set = new HashSet<Symbol.Symbol>();
		TypeDec temp = d;

		for (; d != null; d = d.next) {
			if (set.contains(d.name))
				env.error.error(d.pos, "TypeDec: type redefined");
			set.add(d.name);
			env.tEnv.put(d.name, new NAME(d.name));
		}

		for (d = temp; d != null; d = d.next) {
			((NAME) env.tEnv.get(d.name)).bind(transTy(d.ty));
		}

		for (d = temp; d != null; d = d.next) {
			NAME name_entry = (NAME) env.tEnv.get(d.name);
			if (name_entry.isLoop()) {
				env.error.error(d.pos, "TypeDec: cylic declaration found");
				name_entry.bind(new INT());
			}
		}
		return null;
	}

	// FunctionDec
	Translate.Exp transDec(FunctionDec d) {
		HashSet<Symbol.Symbol> set = new HashSet<Symbol.Symbol>();
		FunctionDec temp = d;

		for (; d != null; d = d.next) {
			if (set.contains(d.name))
				env.error.error(d.pos, "FunctionDec: function name redefined");
			set.add(d.name);
			RECORD para_ty = (RECORD) transTy(new RecordTy(d.pos, d.params));

			Type result_ty = new VOID();
			if (d.result != null)
				result_ty = transTy(d.result);
			Level level = new Level(current_lv, d.name, makeBoolList(d.params));
			env.vEnv.put(d.name, new FuncEntry(level, new Label(d.name),
					para_ty, result_ty));
		}

		for (d = temp; d != null; d = d.next) {
			env.vEnv.beginScope();
			int loopMark_temp = loopMark;
			loopMark = 0;

			if (env.vEnv.get(d.name) instanceof FuncEntry) {
				Level temp_lv = current_lv;
				current_lv = ((FuncEntry) env.vEnv.get(d.name)).level;
				Translate.AccessList acclist = current_lv.formals.tail;
				// 建level时，formals第一项默认为true，是给static_link留下一个寄存器，所以进行变量检查的时候就要从formals.tail开始；
				// Translate.AccessList acclist = current_lv.formals;

				for (FieldList flist = d.params; flist != null; flist = flist.tail, acclist = acclist.tail) {
					Type ty = (Type) env.tEnv.get(flist.typ);
					if (ty == null)
						env.error.error(flist.pos, "type undefined");
					else {
						Translate.Access acc = new Translate.Access(current_lv,
								acclist.head.access);
						env.vEnv.put(flist.name, new VarEntry(acc, ty));
					}
				}
				ExpTy body_ty = transExp(d.body);
				trans.procEntryExit(current_lv, body_ty.exp, !(body_ty.ty.actual() instanceof VOID));
				if (!body_ty.ty.actual().coerceTo(
						((FuncEntry) env.vEnv.get(d.name)).result)
						&& !(body_ty.ty.actual() instanceof NONE)
						&& !(((FuncEntry) env.vEnv.get(d.name)).result.actual() instanceof NONE))
					env.error.error(d.pos, "FunctionDec: type not matched");
				current_lv = temp_lv;
			}
			loopMark = loopMark_temp;
			env.vEnv.endScope();
		}
		return null;
	}

	/*
	 * type_checking declaration ends;
	 */
}

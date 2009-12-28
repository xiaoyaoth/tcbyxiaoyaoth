package Tree;
import Temp.Label;
public class JUMP extends Stm {
  public Expr exp;
  public Temp.LabelList targets;
  public JUMP(Expr e, Temp.LabelList t) {exp=e; targets=t;}
  public JUMP(Temp.Label target) {
      this(new NAME(target), new Temp.LabelList(target,null));
  }
  public ExpList kids() {return new ExpList(exp,null);}
  public Stm build(ExpList kids) {
    return new JUMP(kids.head,targets);
  }
}


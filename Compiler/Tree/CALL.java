package Tree;
import Temp.Temp;
import Temp.Label;
public class CALL extends Expr {
  public Expr func;
  public ExpList args;
  public CALL(Expr f, ExpList a) {func=f; args=a;}
  public ExpList kids() {return new ExpList(func,args);}
  public Expr build(ExpList kids) {
    return new CALL(kids.head,kids.tail);
  }
  
}


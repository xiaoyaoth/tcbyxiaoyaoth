package Tree;
import Temp.Temp;
import Temp.Label;
public class CONST extends Expr {
  public int value;
  public CONST(int v) {value=v;}
  public ExpList kids() {return null;}
  public Expr build(ExpList kids) {return this;}
}


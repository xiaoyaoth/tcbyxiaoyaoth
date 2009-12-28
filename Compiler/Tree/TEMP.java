package Tree;

public class TEMP extends Expr {
  public Temp.Temp temp;
  public TEMP(Temp.Temp t) {temp=t;}
  public ExpList kids() {return null;}
  public Expr build(ExpList kids) {return this;}
}


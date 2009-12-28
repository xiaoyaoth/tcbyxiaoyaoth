package Frame;

import Temp.*;
import Util.*;
import Tree.*;

public abstract class Access {
	public abstract Expr exp(Expr framePtr);
	public abstract Expr expFromStack(Expr stackPtr);
}

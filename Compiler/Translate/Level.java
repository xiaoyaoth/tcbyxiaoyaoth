package Translate;

import Util.*;
import Symbol.*;
import Temp.*;

public class Level {
	public Frame.Frame frame;
	public Level parent;
	public AccessList formals;
	
	public Access staticLink(){return formals.head;}
	
	public Level(Level parent, Symbol name, BoolList fmls){
		this.parent = parent;
		this.frame = parent.frame.newFrame(new Label(name), new BoolList(true, fmls));
		for(Frame.AccessList ac = frame.formals; ac !=null; ac = ac.tail){
			this.formals = new AccessList(new Access(this, ac.head), this.formals );
		}
	}
	
	public Level (Frame.Frame frame){
		this.frame = frame;
	}
	
	public Access allocLocal(boolean escape){
		return new Access(this, frame.allocLocal(escape));
	}
}

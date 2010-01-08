package Translate;

public class ProcFrag extends Frag {
	Tree.Stm body;
	Frame.Frame frame;
	
	ProcFrag(Tree.Stm body, Frame.Frame frame){
		this.body = body;
		this.frame = frame;
	}
}

package Translate;

public class AccessList {
	public Access head;
	public AccessList tail;
	
	public AccessList(Access h, AccessList t){
		this.head = h;
		this.tail = t;
	}
}

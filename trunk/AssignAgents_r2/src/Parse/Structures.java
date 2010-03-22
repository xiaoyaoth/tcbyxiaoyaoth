package Parse;

class Manager extends Agent {
	String empNum = null;

	Manager(String ofc, String emp, String identifer) {
		super(ofc, emp, identifer);
		this.empNum = new String(emp.toString());
	}

	public String toString() {
		return "Manager " + id + " empNum " + empNum + " office " + office;
	}
}

class Employee extends Agent {
	String initial;

	Employee(String ofc, String ini, String identifer) {
		super(ofc, ini, identifer);
		initial = ini;
	}

	public String toString() {
		return "Employee " + id + " initial " + initial + " office " + office;
	}
}

class Agent {
	String office;
	String id;
	String feature;

	Agent(String ofc, String ini, String identifer) {
		office = ofc;
		feature = ini;
		id = identifer;
	}
}

class Message {
	int sender;
	int receiver;
	String content;
	
	Message(int s, int r, String c){
		this.sender = s;
		this.receiver = r;
		this.content = c;
	}
	
/*	public void SendM(int s,int r, String c) {
		Message m = new Message(s, r, c);
		synchronized (newServer.messageBox) {
			newServer.messageBox.put(r, m);
		}
	}

	public Message ReceiveM(int r) {
		synchronized (newServer.messageBox) {
			Message m = newServer.messageBox.get(r);
			return m;
		}
	}*/
}
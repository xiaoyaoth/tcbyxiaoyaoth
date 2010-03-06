package Parse;

import java.util.HashSet;
import java.util.Set;

class Manager extends Agent {
	String office = null;
	String id = null;
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
//	Level lv;

	Agent(String ofc, String ini, String identifer) {
		office = ofc;
		feature = ini;
		id = identifer;
	}
}

class Level {
	String current;
	Set<String> co = new HashSet<String>();
	Set<String> sub = new HashSet<String>();
	Set<String> sup = new HashSet<String>();
	
	Level (String cu){
		this.current = cu;
	}
}
package Parse;

import java.util.ArrayList;

public class Client extends Thread {
	private int JVM_id;
	public static int JVM_counter;
	public int pointer;
	public static ArrayList<Server> cases = new ArrayList<Server>();

	Client() {
		JVM_id = JVM_counter;
		System.out.println("JVM " + JVM_id + " starts");
	}

	public static void main(String[] args) {
		Client c = new Client();
		JVM_counter++;
		c.start();
	}

	public void run() {
		Integer id = JVM_id;
		pointer = 0;
		while (true) {
			if (cases.size()-1>=pointer) {
				Server oneSenario = cases.get(pointer);
				if (oneSenario.conv_fini) {
					ArrayList<ArrayList<String>> oneCase = oneSenario.caseTable;
					System.out.println(oneCase);
					for (int i = 0; i < oneCase.size(); i++) {
						Agent ag = null;
						ArrayList<String> one = oneCase.get(i);
						if (oneCase.get(i).get(0).equals(id.toString())) {
							if (oneCase.get(i).get(2).equals("Manager")) {
								ag = new Manager(one.get(1), one.get(4), one
										.get(3));
							}
							if (one.get(2).equals("Employee")) {
								ag = new Employee(one.get(1), one.get(4), one
										.get(3));
							}
							synchronized (oneSenario.map) {
								oneSenario.map.put(ag.id, ag);
							}
						}
						System.out.println(ag);
					}
					System.out
							.println("[This scenario is completely arranged!]");
					System.out.println();
					pointer++;
				}
			} else {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
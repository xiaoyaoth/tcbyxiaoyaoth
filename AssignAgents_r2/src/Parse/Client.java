package Parse;

import java.util.ArrayList;

public class Client extends Thread {
	private int JVM_id;
	public static int JVM_counter;
	public static int fini;
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
		while (true) {
			if (!cases.isEmpty()) {
				Server oneSenario = cases.get(0);
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
					fini++;
					while (fini != JVM_counter) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					fini = 0;
					synchronized (cases) {
						cases.remove(0);
					}
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
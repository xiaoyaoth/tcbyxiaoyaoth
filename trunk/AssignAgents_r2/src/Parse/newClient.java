package Parse;

import java.util.ArrayList;

public class newClient extends Thread {
	private int JVM_id;
	public static int JVM_COUNTER;
	public static boolean remove;

	newClient() {
		JVM_id = JVM_COUNTER;
		System.out.println("JVM " + JVM_id + " starts");
	}

	public static void main(String[] args) {
		newClient c = new newClient();
		JVM_COUNTER++;
		c.start();
	}

	public void run() {
		Integer id = JVM_id;
		while (true) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(newServer.conv_fini);
			if (!newServer.outbox.isEmpty() && newServer.conv_fini) {
				ArrayList<ArrayList<String>> oneCase = newServer.outbox.get(0);
				remove = false;
				System.out.println(oneCase);
				for (int i = 0; i < oneCase.size(); i++) {
					ArrayList<String> one = oneCase.get(i);
					if (oneCase.get(i).get(0).equals(id.toString())) {
						if (oneCase.get(i).get(2).equals("Manager"))
							System.out.println(new Manager(one.get(1), one
									.get(4), one.get(3)));
						if (one.get(2).equals("Employee")) {
							System.out.println(new Employee(one.get(1), one
									.get(4), one.get(3)));
						}
					}
				}
				synchronized (newServer.outbox) {
					if (remove == false) {
						newServer.outbox.remove(0);
						remove = true;
						newServer.outbox.trimToSize();
					}
				}
			}
		}
	}
}
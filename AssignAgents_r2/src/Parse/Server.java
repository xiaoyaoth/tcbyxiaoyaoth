package Parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Server {
	public boolean conv_fini = false;
	public ArrayList<ArrayList<String>> caseTable;
	public Map<String, Agent> map;

	Server() throws ParserConfigurationException, SAXException, IOException {
		caseTable = new ArrayList<ArrayList<String>>();
		map = new HashMap<String, Agent>();
		new Parse();
		System.out.println(Parse.table);
		for (int i = 0; i < Parse.table.size(); i++) {
			Integer jvm_id = (int) (Math.random() * Client.JVM_counter);
			Parse.table.get(i).add(0, jvm_id.toString());
		}
		caseTable = Parse.table;
		System.out.println(caseTable);
	}

	public void setCoviFini() {
		conv_fini = true;
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Waiting for all clients to start");
		try {
			Server one = new Server();
			synchronized (Client.cases) {
				Client.cases.add(one);
			}
			one.setCoviFini();
			while (input.next().equals("y")){
				System.out.println("Input the \'id\' to direct to an agent");
				String id = input.next();
				Agent ag = one.map.get(id);
				try{
				System.out.println(ag+" HashCode:" + ag.hashCode());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

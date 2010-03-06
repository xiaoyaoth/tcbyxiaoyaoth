package Parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class newServer {
	private static Scanner input = new Scanner(System.in);
	public static boolean conv_fini = false;
	public static ArrayList<ArrayList<ArrayList<String>>> outbox = new ArrayList<ArrayList<ArrayList<String>>>();

	public static void main(String[] args) {		
		System.out.println("Waiting for all clients to start");
		try {
			new newParse();
			System.out.println(newParse.table);
			for (int i = 0; i < newParse.table.size(); i++) {
				Integer jvm_id = (int) (Math.random() * newClient.JVM_COUNTER);
				newParse.table.get(i).add(0, jvm_id.toString());
			}
			synchronized (outbox) {
				outbox.add(newParse.table);
			}
			System.out.println(outbox);
			conv_fini = true;
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

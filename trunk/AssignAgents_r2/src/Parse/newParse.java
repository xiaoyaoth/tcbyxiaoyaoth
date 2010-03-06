package Parse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class newParse {

	static ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	static int instance_amount = -1;
	static boolean roleinfo = false;
	static boolean info = false;
	static boolean role = false;
	static boolean instance = false;
	static String office;
	static int index;
	static ArrayList<String> roles = new ArrayList<String>();

	newParse() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder = domfac.newDocumentBuilder();
		InputStream is = new FileInputStream(
		// "F:\\我的文件夹\\PRP\\tryAdAgain\\tc-config.xml");
				"F:\\我的文件夹\\PRP\\AssignTasks\\origin\\snr.xml");
		Document doc = dombuilder.parse(is);
		printNode(doc, 0);
	}

	public static void printNode(Node node, int count) {
		String tmp = "";
		for (int i = 0; i < count; i++)
			tmp += "  ";

		if (node != null) {
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null)
				for (int i = 0; i < attrs.getLength(); i++) {
					printNode(attrs.item(i), count + 1);
					Node n = attrs.item(i);
					if (n.getNodeName().equals("name") && roleinfo)
						roles.add(n.getNodeValue());
					if (n.getNodeName().equals("name") && info)
						office = n.getNodeValue();
					if (n.getNodeName().equals("index") && role) {
						index = Integer.parseInt(n.getNodeValue());
					}
					if (n.getNodeName().equals("id") && instance) {
						table.get(instance_amount).add(office);
						table.get(instance_amount).add(roles.get(index));
						table.get(instance_amount).add(n.getNodeValue());
					}
					if (n.getNodeName().equals("val") && instance) {
						table.get(instance_amount).add(n.getNodeValue());
					}
				}

			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node n = childNodes.item(i);
				if (n.getNodeName().equals("RoleInfo")) {
					roleinfo = true;
					info = false;
					role = false;
					instance = false;
				}
				if (n.getNodeName().equals("Info")) {
					roleinfo = false;
					info = true;
					role = false;
					instance = false;
				}
				if (n.getNodeName().equals("Role")) {
					roleinfo = false;
					info = false;
					role = true;
					instance = false;
				}
				if (n.getNodeName().equals("Instance")) {
					roleinfo = false;
					info = false;
					role = false;
					instance = true;
					table.add(new ArrayList<String>());
					instance_amount++;
				}
				printNode(childNodes.item(i), count + 1);
			}
		}
	}

	public static void main(String[] args) {
		try {
			new newParse();
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

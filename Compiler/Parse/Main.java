package Parse;

public class Main {

	public static void main(String argv[]) {
		// String filename = argv[0];
		String filename = "F:\\�ҵ��ļ���\\�μ�����ҵ\\����γ����\\TigerBook\\sources\\testcases\\";
		mode2(filename);
	}

	public static void mode1(String fname) {
		for (int i = 2; i < 49; i++) {
			String temp = fname +"test"+ i + ".tig";
			//System.out.println("test" + i + "");
			new Parse(temp);
			//System.out.println();
		}
	}
	
	public static void mode2(String fname){
		//String temp = fname + "test" + 17 + ".tig";
		String temp = fname +"queens.tig";
		new Parse(temp);
	}
}
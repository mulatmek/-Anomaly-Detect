package test;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	Map<Integer,Command> commands;
	DefaultIO dio;
	Commands c;

	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio);
		commands=new HashMap<>();
		commands.put(1,c.new FIRST());
		commands.put(2,c.new SECOND());
		commands.put(3,c.new THIRD());
		commands.put(4,c.new FOURTH());
		commands.put(5,c.new FIFTH());
		commands.put(6,c.new EXIT())		;
		// implement
	}
	public static boolean isNumber (String s){
		try {
			int a=Integer.parseInt(s);

		}catch (Exception e){
			return false;
		}
		return true;
	}

	public void start()  {
		String UserInput;
		int num=1;
		do {
		//	System.out.println("menu");
			dio.write("Welcome to the Anomaly Detection Server.\nPlease choose an option:\n");
			for (int i=0; i<commands.size(); i++) {
				dio.write((i+1)+". "+commands.get(i+1).description+"\n");
			}

			String input= dio.readText();
			try {
				if (isNumber(input)) {
					num = Integer.parseInt(input);
					commands.get(num).execute();
				}
			}
			catch (IOException e){
				e.printStackTrace();
			}
		} while (num != 6);
	}
}


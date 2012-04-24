package se.insella.bolagetscraper;

import java.util.LinkedList;
import java.util.List;

public class ScalaRunner {
	public static void main(String[] args) {
		if (args.length == 0) {
			new GUIMain(ConfigLoader.defaultConfig());
		} else {
			int offset = 0;
			if (isCommandArgument(args[0])) {
				offset++;
			}
			String[] args2 = new String[args.length-offset];
			for (int i = 0; i<args2.length;i++) {
				args2[i] = args[i+offset];
			}
			Options[] query = ConfigLoader.parseAndValidate(args2);
		
		}
	}
	
	private static boolean isCommandArgument(String s) {
		switch (s) {
		case"w":
			return true;
		default:
			return false;
		}
	}
}
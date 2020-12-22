package org.jpostdb.proteome.util;

public class StringUtil {
	public static String nvl(String string) {
		if(string == null) {
			return "";
		}
		return string.trim();
	}

	public static String getOption(String[] args, String option) {
		boolean flag = false;
		String value = null;

		for(String arg : args) {
			if(flag) {
				value = arg;
				flag = false;
			}
			else {
				if(arg.equals("-" + option) || arg.equals("--" + option)) {
					flag = true;
				}
			}
		}

		return value;
	}
}


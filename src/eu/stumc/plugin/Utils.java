package eu.stumc.plugin;

public class Utils {
	
	public static boolean intToBool(int i) {
		return i != 0;
	}
	
	public static int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static String calculateDaysDifference(long timestamp) {
		return Long.toString(((timestamp - System.currentTimeMillis()) / 86400000) + 1);
	}
	
}

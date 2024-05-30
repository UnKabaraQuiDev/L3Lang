package lu.pcy113.l3.utils;

public final class L3Utils {

	public static String getPackageName(String str) {
		if(str.contains(".")) {
			return FileUtils.removeExtension(str);
		}else {
			return "";
		}
	}

}

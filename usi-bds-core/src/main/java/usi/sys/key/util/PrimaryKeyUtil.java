package usi.sys.key.util;


public class PrimaryKeyUtil {
	
	/**
	 * @param name
	 * @return
	 */
	public static Long getPrimaryKey(String name) {
		SingleSequence sequence = PrimaryKeyFactory.getSequence(name);
	    long num = sequence.getNextVal(name);
		return num;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public static Long getPrimaryKey(Class<?> clazz) {
		SingleSequence sequence = PrimaryKeyFactory.getSequence(clazz.getSimpleName());
	    long num = sequence.getNextVal(clazz.getSimpleName());
		return num;
	}
}
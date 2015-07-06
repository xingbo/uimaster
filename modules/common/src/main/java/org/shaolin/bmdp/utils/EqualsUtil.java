package org.shaolin.bmdp.utils;

/**
 * This class contains utility methods to check equation for various data types
 */
public class EqualsUtil {
	/**
	 * Check equation for two booleans
	 * 
	 * @param b1
	 *            boolean 1
	 * @param b2
	 *            boolean 2
	 * @return <code>true</code> if b1 equals b2, <code>false</code> otherwise
	 */
	public static boolean equals(boolean b1, boolean b2) {
		return b1 == b2;
	}

	/**
	 * Check equation for two bytes
	 * 
	 * @param b1
	 *            byte 1
	 * @param b2
	 *            byte 2
	 * @return <code>true</code> if b1 equals b2, <code>false</code> otherwise
	 */
	public static boolean equals(byte b1, byte b2) {
		return b1 == b2;
	}

	/**
	 * Check equation for two characters
	 * 
	 * @param c1
	 *            character 1
	 * @param c2
	 *            character 2
	 * @return <code>true</code> if c1 equals c2, <code>false</code> otherwise
	 */
	public static boolean equals(char c1, char c2) {
		return c1 == c2;
	}

	/**
	 * Check equation for two doubles
	 * 
	 * @param d1
	 *            double 1
	 * @param d2
	 *            double 2
	 * @return <code>true</code> if d1 equals d2, <code>false</code> otherwise
	 */
	public static boolean equals(double d1, double d2) {
		return Double.doubleToRawLongBits(d1) == Double.doubleToRawLongBits(d2);
	}

	/**
	 * Check equation for two floats
	 * 
	 * @param f1
	 *            float 1
	 * @param f2
	 *            float 2
	 * @return <code>true</code> if f1 equals f2, <code>false</code> otherwise
	 */
	public static boolean equals(float f1, float f2) {
		return Float.floatToRawIntBits(f1) == Float.floatToRawIntBits(f2);
	}

	/**
	 * Check equation for two integers
	 * 
	 * @param i1
	 *            integer 1
	 * @param i2
	 *            integer 2
	 * @return <code>true</code> if i1 equals i2, <code>false</code> otherwise
	 */
	public static boolean equals(int i1, int i2) {
		return i1 == i2;
	}

	/**
	 * Check equation for two longs
	 * 
	 * @param l1
	 *            long 1
	 * @param l2
	 *            long 2
	 * @return <code>true</code> if l1 equals l2, <code>false</code> otherwise
	 */
	public static boolean equals(long l1, long l2) {
		return l1 == l2;
	}

	/**
	 * Check equation for two shorts
	 * 
	 * @param s1
	 *            short 1
	 * @param s2
	 *            short 2
	 * @return <code>true</code> if s1 equals s2, <code>false</code> otherwise
	 */
	public static boolean equals(short s1, short s2) {
		return s1 == s2;
	}

	/**
	 * Check equation for two objects
	 * 
	 * @param o1
	 *            object 1
	 * @param o2
	 *            object 2
	 * @return <code>true</code> if s1 == s2 == null or s1 equals s2,
	 *         <code>false</code> otherwise
	 */
	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	public static boolean checkEquals(boolean b1, boolean b2) {
		return equals(b1, b2);
	}

	public static boolean checkEquals(byte b1, byte b2) {
		return equals(b1, b2);
	}

	public static boolean checkEquals(char c1, char c2) {
		return equals(c1, c2);
	}

	public static boolean checkEquals(double d1, double d2) {
		return equals(d1, d2);
	}

	public static boolean checkEquals(float f1, float f2) {
		return equals(f1, f2);
	}

	public static boolean checkEquals(int i1, int i2) {
		return equals(i1, i2);
	}

	public static boolean checkEquals(long l1, long l2) {
		return equals(l1, l2);
	}

	public static boolean checkEquals(short s1, short s2) {
		return equals(s1, s2);
	}

	public static boolean checkEquals(Object o1, Object o2) {
		return equals(o1, o2);
	}

	private EqualsUtil() {
	}

}

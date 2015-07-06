package org.shaolin.bmdp.utils;

public class HashUtil {
	public static int hashCode(boolean b) {
		return b ? 1231 : 1237;
	}

	public static int hashCode(byte b) {
		return b;
	}

	public static int hashCode(char c) {
		return c;
	}

	public static int hashCode(double d) {
		long bits = Double.doubleToLongBits(d);
		return (int) (bits ^ (bits >>> 32));
	}

	public static int hashCode(float f) {
		return Float.floatToIntBits(f);
	}

	public static int hashCode(int i) {
		return i;
	}

	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}

	public static int hashCode(short s) {
		return s;
	}

	public static int hashCode(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	private HashUtil() {
	}

}

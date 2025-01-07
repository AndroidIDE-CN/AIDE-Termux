/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package com.termux.shared.logger;

public class Log {
	public static final int ASSERT = 0x7 /*7*/;

	public static final int DEBUG = 0x3 /*3*/;

	public static final int ERROR = 0x6 /*6*/;

	public static final int INFO = 0x4 /*4*/;

	public static final int VERBOSE = 0x2 /*2*/;

	public static final int WARN = 0x5 /*5*/;

	public static final boolean hasZeroAicyLog;

	static {
		Class<?> zeroAicyLogClass = null;
		try {
			zeroAicyLogClass = Class.forName("io.github.zeroaicy.util.Log");
		} catch (Throwable e) {
		}

		hasZeroAicyLog = zeroAicyLogClass != null;
	}

	public static void v(String tag, String msg) {
		if (hasZeroAicyLog) {
			io.github.zeroaicy.util.Log.v(tag, msg);
		}
		android.util.Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (hasZeroAicyLog) {
			io.github.zeroaicy.util.Log.d(tag, msg);
		}
		android.util.Log.v(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (hasZeroAicyLog) {
			io.github.zeroaicy.util.Log.i(tag, msg);
		}
		android.util.Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (hasZeroAicyLog) {
			io.github.zeroaicy.util.Log.w(tag, msg);
		}
		android.util.Log.v(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (hasZeroAicyLog) {
			io.github.zeroaicy.util.Log.e(tag, msg);
		}
		android.util.Log.v(tag, msg);
	}

}


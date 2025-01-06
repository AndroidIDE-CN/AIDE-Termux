



package io.github.zeroaicy.util.reflect;

import android.os.Build;
import io.github.zeroaicy.util.Log;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HiddenApiBypass {

	private static Object sVmRuntime;
	private static Method setHiddenApiExemptionsMethod;
	private static Object currentActivityThread;

	public static Object getCurrentActivityThread() {
		return currentActivityThread;
	}
	
	static  {
		try {
			if ( !initializedFromZeroAicy() ) {
				boolean initializedFromEirv = initializedFromEirv();
				Log.d("HiddenApiBypass initializedFromEirv", initializedFromEirv);
			} else {
				Log.d("HiddenApiBypass initializedFromZeroAicy OK");
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static boolean initializedFromZeroAicy( ) {
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.P ) {
			return true;
		}
		
		try {
			Method forName = Class.class.getDeclaredMethod("forName", String.class);
			Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

			Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
			Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
			HiddenApiBypass.setHiddenApiExemptionsMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
			HiddenApiBypass.sVmRuntime = getRuntime.invoke(null);

			
			boolean hiddenApiExemptions = setHiddenApiExemptions("L");
			// 验证一下
			HiddenApiBypass.currentActivityThread = ReflectPie.onClass("android.app.ActivityThread").call("currentActivityThread").get();
			System.err.println("解除反射限制测试 currentActivityThread " + currentActivityThread);
			
			return hiddenApiExemptions;
		}
		catch (final Throwable e) {
			e.printStackTrace();
		}
		return false;

	}
	/*
	 * @author eirv
	 */
	private static final boolean initializedFromEirv( ) {
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.P ) {
			return true;
		}
		try {
			UnsafeX unsafe = UnsafeX.getUnsafe();
			//assert unsafe.unsafe != null;

			Method[] stubs = Compiler.class.getDeclaredMethods();

			Method first = stubs[0];
			Method second = stubs[1];
			long size = unsafe.getLong(second, 24) - unsafe.getLong(first, 24);

			int addrSize = unsafe.addressSize();

			long methods = unsafe.getLong(Class.forName("dalvik.system.VMRuntime"), 48);

			long count = addrSize == 8 ? unsafe.getLong(methods) : unsafe.getInt(methods);

			methods += addrSize;

			for ( long j = 0, done = 0; count > j; j++ ) {
				long method = j * size + methods;
				unsafe.putLong(first, 24, method);
				String name = first.getName();

				if ( !"getRuntime".equals(name) && !"setHiddenApiExemptions".equals(name) ) continue;
				// 我下面这几行是把方法移出隐藏 api 列表
				// first 方法可以直接调用的

				long addr = method + 4;
				int acc = unsafe.getInt(addr);

				unsafe.putInt(addr, acc | 0x10000000);

				if ( ++done == 2 ) break;
			}


			Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");

			Method getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime");

			HiddenApiBypass.setHiddenApiExemptionsMethod = vmRuntimeClass.getDeclaredMethod("setHiddenApiExemptions", String[].class);
			HiddenApiBypass.sVmRuntime = getRuntime.invoke(null);

			boolean hiddenApiExemptions = setHiddenApiExemptions("L");
			// 验证一下
			HiddenApiBypass.currentActivityThread = ReflectPie.onClass("android.app.ActivityThread").call("currentActivityThread").get();
			System.err.println("解除反射限制测试 currentActivityThread " + currentActivityThread);
			
			return hiddenApiExemptions;
		}
		catch (final Throwable e) {
			Log.e(TAG, "initializedFromEirv", e);  
		}
        
		return false;
    }
	public static boolean setHiddenApiExemptions( String... signaturePrefixes ) {

		//Log.d(TAG, "setHiddenApiExemptions被调用路径", new Throwable());
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.P ) {
			return true;
		}
		
		if ( HiddenApiBypass.sVmRuntime == null || HiddenApiBypass.setHiddenApiExemptionsMethod == null ) {
			return false;
		}
		try {
			HiddenApiBypass.setHiddenApiExemptionsMethod.invoke(HiddenApiBypass.sVmRuntime, new Object[]{signaturePrefixes});
			return true;
		}
		catch (final Throwable e) {
			Log.e(TAG, "setHiddenApiExemptions", e);
			return false;
		}
	}

	private static final String TAG = "HiddenApiBypass";

	private static final Set<String> signaturePrefixes = new HashSet<>();
	
	public static boolean addHiddenApiExemptions( String... signaturePrefixes ) {
        HiddenApiBypass.signaturePrefixes.addAll(Arrays.asList(signaturePrefixes));
        String[] strings = new String[HiddenApiBypass.signaturePrefixes.size()];
        HiddenApiBypass.signaturePrefixes.toArray(strings);

        return setHiddenApiExemptions(strings);
    }
	
	public static boolean clearHiddenApiExemptions( ) {
        HiddenApiBypass.signaturePrefixes.clear();
        return setHiddenApiExemptions();
    }
}

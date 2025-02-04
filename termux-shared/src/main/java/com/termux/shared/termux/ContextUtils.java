/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package com.termux.shared.termux;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import io.github.zeroaicy.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContextUtils {

	
	static {
		
		// 解除反射限制
		HiddenApiBypass.setHiddenApiExemptions("L");
		
	}
	
	public static final String TAG = "ContextUtils";
	
	static Context applicationContext;

	public static void setApplicationContext(Context applicationContext) {
		ContextUtils.applicationContext = applicationContext;
	}

	public static Context getApplicationContext() {

		if (ContextUtils.applicationContext == null) {
			ContextUtils.applicationContext = getApplication();
		}
		return ContextUtils.applicationContext;
	}
	
	
	public static Application getApplication() {
		try {
			if (ContextUtils.applicationContext instanceof Application) {
				return (Application) ContextUtils.applicationContext;
			} else {
				Class<?> activityThreadClass = Class.forName("android.app.ActivityThread"); 
				java.lang.reflect.Method currentPackage = activityThreadClass.getMethod("currentActivityThread");
				
				Object currentActivityThread = currentPackage.invoke(null);

				Application application = (Application) activityThreadClass.getMethod("getApplication").invoke(currentActivityThread);
				
				ContextUtils.applicationContext = application;
				return application;
			}
		} catch (Throwable e) {
			Log.e(TAG, "getApplication()", e);
			return null;
		}
	}
	
	
	/**
	 * 取得Context对象
	 * PS:必须在主线程调用
	 * 两种方法，极大力度保证返回值不为null
	 * 1.android.app.ActivityThread.currentActivityThread()getApplication()
	 * 2.反射构造(兜底)
	 * @return Context
	 */
	public static Context getContext(){
		if ( ContextUtils.applicationContext == null || !(ContextUtils.applicationContext instanceof Application) ){
			Application application = getApplication();
			if ( application != null ){
				ContextUtils.applicationContext = application;
			}
		}
		if ( ContextUtils.applicationContext == null ){
			//ContextUtils.applicationContext = createAppContext();
		}
		return applicationContext;
	}
	
	public static class HiddenApiBypass {

		private static Object sVmRuntime;
		private static Method setHiddenApiExemptionsMethod;
		private static Object currentActivityThread;

		public static Object getCurrentActivityThread() {
			return currentActivityThread;
		}
		
		public static Object getActivityThread() throws Throwable{
			Class<?> activityThreadClass = Class.forName("android.app.ActivityThread"); 
			java.lang.reflect.Method currentPackage = activityThreadClass.getMethod("currentActivityThread");

			Object currentActivityThread = currentPackage.invoke(null);
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
				HiddenApiBypass.currentActivityThread = getActivityThread();
				
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
				HiddenApiBypass.currentActivityThread = getActivityThread();
				
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
	
	
	public static class UnsafeX{
		private static Class<?> UnsafeClass;

		private static Method getUnsafe;

		private static Method addressSize;

		private static Method getInt;
		private static Method getInt1;

		private static Method getLong;
		private static Method getLong1;


		private static Method putInt;
		private static Method putInt1;

		private static Method putLong;
		private static Method putObject;


		static{
			try{

				UnsafeClass = Class.forName("sun.misc.Unsafe");
				getUnsafe = UnsafeClass.getDeclaredMethod("getUnsafe");

				addressSize = UnsafeClass.getDeclaredMethod("addressSize");

				getInt = UnsafeClass.getDeclaredMethod("getInt", long.class);
				getInt1 = UnsafeClass.getDeclaredMethod("getInt", Object.class, long.class);

				getLong = UnsafeClass.getDeclaredMethod("getLong", long.class);
				getLong1 = UnsafeClass.getDeclaredMethod("getLong", Object.class, long.class);

				putInt = UnsafeClass.getDeclaredMethod("putInt", long.class, int.class);
				putInt1 = UnsafeClass.getDeclaredMethod("putInt", Object.class, long.class, int.class);

				putLong = UnsafeClass.getDeclaredMethod("putLong", Object.class, long.class, long.class);
				putObject = UnsafeClass.getDeclaredMethod("putObject", Object.class, long.class, Object.class);

			}
			catch (Throwable e){

			}
		}

		Object unsafe;
		private UnsafeX(Object unsafe){
			this.unsafe = unsafe;
		}

		public int addressSize() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return (Integer)addressSize.invoke(this.unsafe);

		}

		public int getInt(long address) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return (Integer)getInt.invoke(this.unsafe, address);
		}

		public int getInt(Object obj, long offset) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return (Integer)getInt1.invoke(this.unsafe, obj, offset);
		}

		public long getLong(long address) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return (Long)getLong.invoke(this.unsafe, address);
		}

		public long getLong(Object obj, long offset) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return (Long)getLong1.invoke(this.unsafe, obj, offset);
		}

		public static UnsafeX getUnsafe() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return new UnsafeX(getUnsafe.invoke(null));
		}

		public void putInt(long address, int x) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			putInt.invoke(this.unsafe, address, x);
		}

		public void putInt(Object obj, long offset, int newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			putInt1.invoke(this.unsafe, obj, offset, newValue);
		}

		public void putLong(Object obj, long offset, long newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			putLong.invoke(this.unsafe, obj, offset, newValue);
		}

		public void putObject(Object obj, long offset, Object newValue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			putObject.invoke(this.unsafe, obj, offset, newValue);
		}



	}
}


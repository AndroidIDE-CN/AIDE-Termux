package io.github.zeroaicy.util;
import io.github.zeroaicy.util.crash.CrashApplication;
import android.content.Context;

public class DebugUtil{
	
	
	public static void debug(){
		debug(ContextUtil.getContext(), false);
	}
	
	public static void debug(Context context){
		debug(context, true);
	}
	public static void debug(Context context, boolean isChangerLog){
		Log.SetSystemOut(true);
		
		if( isChangerLog){
			FileUtil.init();
		}
		Log.enable(FileUtil.LogCatPath);
		CrashApplication.CrashInit(context);
	}
	
	public static void notDebug(){
		Log.disable();
	}

}

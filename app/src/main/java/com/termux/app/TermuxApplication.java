package com.termux.app;

import android.app.Application;
import android.content.Context;

import com.termux.BuildConfig;
import com.termux.shared.errors.Error;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxBootstrap;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.crash.TermuxCrashUtils;
import com.termux.shared.termux.file.TermuxFileUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;
import com.termux.shared.termux.shell.command.environment.TermuxShellEnvironment;
import com.termux.shared.termux.shell.am.TermuxAmSocketServer;
import com.termux.shared.termux.shell.TermuxShellManager;
import com.termux.shared.termux.theme.TermuxThemeUtils;
import com.termux.shared.termux.ContextUtils;

public class TermuxApplication extends Application {

	private static final String LOG_TAG = "TermuxApplication";

	// AIDE-Termux默认 apt-android-7
	public static String TERMUX_PACKAGE_VARIANT = "apt-android-7";

	public void onCreate() {
		super.onCreate();

		init(this);
	}
	
	private static boolean inited = false;
	
	public static void init(Context context) {
		if( TermuxApplication.inited ){
			return;
		}
		// 已初始化
		inited = true;
		
		Context applicationContext = context.getApplicationContext();
		ContextUtils.setApplicationContext(applicationContext);
		
		// Set crash handler for the app
		TermuxCrashUtils.setDefaultCrashHandler(applicationContext);

		// Set log config for the app
		setLogConfig(context);

		Logger.logDebug("Starting Application");

		// Set TermuxBootstrap.TERMUX_APP_PACKAGE_MANAGER and TermuxBootstrap.TERMUX_APP_PACKAGE_VARIANT
		TermuxBootstrap.setTermuxPackageManagerAndVariant(TermuxApplication.TERMUX_PACKAGE_VARIANT);

		// Init app wide SharedProperties loaded from termux.properties
		TermuxAppSharedProperties properties = TermuxAppSharedProperties.init(context);

		// Init app wide shell manager
		TermuxShellManager shellManager = TermuxShellManager.init(context);

		// Set NightMode.APP_NIGHT_MODE
		TermuxThemeUtils.setAppNightMode(properties.getNightMode());

		// Check and create termux files directory. If failed to access it like in case of secondary
		// user or external sd card installation, then don't run files directory related code
		Error error = TermuxFileUtils.isTermuxFilesDirectoryAccessible(applicationContext, true, true);
		boolean isTermuxFilesDirectoryAccessible = error == null;
		if (isTermuxFilesDirectoryAccessible) {
			Logger.logInfo(LOG_TAG, "Termux files directory is accessible");
			/*
			 error = TermuxFileUtils.isAppsTermuxAppDirectoryAccessible(true, true);
			 if (error != null) {
			 Logger.logErrorExtended(LOG_TAG, "Create apps/termux-app directory failed\n" + error);
			 return;
			 }
			
			 // Setup termux-am-socket server
			 TermuxAmSocketServer.setupTermuxAmSocketServer(context);
			 */
		} else {
			Logger.logErrorExtended(LOG_TAG, "Termux files directory is not accessible\n" + error);
		}

		// Init TermuxShellEnvironment constants and caches after everything has been setup including termux-am-socket server
		TermuxShellEnvironment.init(applicationContext);

		if (isTermuxFilesDirectoryAccessible) {
			TermuxShellEnvironment.writeEnvironmentToFile(applicationContext);
		}
	}

	public static void setLogConfig(Context context) {
		Logger.setDefaultLogTag(TermuxConstants.TERMUX_APP_NAME);

		// Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
		TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context);
		if (preferences == null)
			return;
		preferences.setLogLevel(null, preferences.getLogLevel());
	}

}



package io.github.zeroaicy.util;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.TextUtils;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Mock Log implementation for testing on non android host.
 * @ZeroAicy
 */
public final class Log {

	public static void printlnFields(Object x) {
		if (x == null) {
			return;
		}
		for (Map.Entry<String, ReflectPie> entry : ReflectPie.on(x).fields().entrySet()) {
			Log.println("key: " + entry.getKey() + " value: " + entry.getValue().get());
		}
	}

	/*PrintStream API*/
	public static void flush() {
		if (isOut && Log.getLog() != null)
			Log.getLog().flush();
	}

	public static void close() {
		if (isOut && Log.getLog() != null)
			Log.getLog().close();
	}

	public static void write(int b) {
		if (isOut && Log.getLog() != null)
			Log.getLog().write(b);
	}

	public static void write(byte[] buf, int off, int len) {
		if (isOut && Log.getLog() != null)
			Log.getLog().write(buf, off, len);
	}

	public static void print(boolean b) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(b);
	}

	public static void print(char c) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(c);
	}

	public static void print(int i) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(i);
	}

	public static void print(long l) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(l);
	}

	public static void print(float f) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(f);
	}

	public static void print(double d) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(d);
	}

	public static void print(char[] s) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(s);
	}
	public static void print(char[] s, int fromIndex, int toIndex) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(String.valueOf(s, fromIndex, toIndex));
	}

	public static void print(String s) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(s);
	}

	public static void print(Object obj) {
		if (isOut && Log.getLog() != null)
			Log.getLog().print(obj);
	}

	public static void println() {
		if (isOut && Log.getLog() != null)
			Log.getLog().println();
	}

	public static void println(boolean x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(char x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(int x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(long x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(float x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(double x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(char[] x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(char[] s, int fromIndex, int toIndex) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(String.valueOf(s, fromIndex, toIndex));
	}

	public static void println(String x) {
		//缓存
		if (!isOut && preMsgList != null && preMsgList.size() < maxPreNumber) {
			preMsgList.add(x);
		}
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}

	public static void println(Throwable e) {

		if (isOut && Log.getLog() != null)
			e.printStackTrace(Log.getLog());
	}
	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;

	private Log() {
	}

	public static int d(String TAG, Object... objects) {
		StringBuilder mStringBuilder = new StringBuilder();
		if (objects != null) {
			for (Object obj : objects) {
				if (obj instanceof Throwable) {
					mStringBuilder.append(getStackTraceString((Throwable) obj));
					mStringBuilder.append("  ");
					continue;
				}
				mStringBuilder.append(obj);
				mStringBuilder.append("  ");
			}
		}
		return println(LOG_ID_MAIN, DEBUG, TAG, mStringBuilder.toString());
	}

	public static void println(Object x) {
		if (isOut && Log.getLog() != null)
			Log.getLog().println(x);
	}
	public static void println(StackTraceElement[] stackTrace) {
		if (isOut && Log.getLog() != null) {
			for (int i = 2; i < stackTrace.length - 1; i++) {
				Log.println(stackTrace[i].toString());
			}
			Log.println();
		}
	}
	public static void println(StackTraceElement[] stackTrace, int off, int endOff) {
		if (isOut && Log.getLog() != null) {
			for (int i = off; i < stackTrace.length - endOff - 1; i++) {
				Log.println(stackTrace[i].toString());
			}
			Log.println();
		}
	}
	public static void printlnStack(int off, int endOff) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		println(stackTrace, off + 2, endOff);
	}

	public static void printlnStack() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		println(stackTrace, 2, 2);
	}
	/**
	 * Send a {@link #VERBOSE} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int v(String tag, String msg) {
		return println(LOG_ID_MAIN, VERBOSE, tag, msg);
	}

	/**
	 * Send a {@link #VERBOSE} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int v(String tag, String msg, Throwable tr) {
		return println(LOG_ID_MAIN, VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send a {@link #DEBUG} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int d(String tag, String msg) {
		return println(LOG_ID_MAIN, DEBUG, tag, msg);
	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int d(String tag, String msg, Throwable tr) {
		return println(LOG_ID_MAIN, DEBUG, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send an {@link #INFO} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int i(String tag, String msg) {
		return println(LOG_ID_MAIN, INFO, tag, msg);
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int i(String tag, String msg, Throwable tr) {
		return println(LOG_ID_MAIN, INFO, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Send a {@link #WARN} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int w(String tag, String msg) {
		return println(LOG_ID_MAIN, WARN, tag, msg);
	}

	/**
	 * Send a {@link #WARN} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int w(String tag, String msg, Throwable tr) {
		return println(LOG_ID_MAIN, WARN, tag, msg + '\n' + getStackTraceString(tr));
	}

	/*
	 * Send a {@link #WARN} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param tr An exception to log
	 */
	public static int w(String tag, Throwable tr) {
		return println(LOG_ID_MAIN, WARN, tag, getStackTraceString(tr));
	}

	/**
	 * Send an {@link #ERROR} log message.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 */
	public static int e(String tag, String msg) {
		return println(LOG_ID_MAIN, ERROR, tag, msg);
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @param tr An exception to log
	 */
	public static int e(String tag, String msg, Throwable tr) {
		return println(LOG_ID_MAIN, ERROR, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * @param tr An exception to log
	 */
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		// This is to reduce the amount of log spew that apps do in the non-error
		// condition of the network being unavailable.
		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	/**
	 * Low-level logging call.
	 * @param priority The priority/type of this log message
	 * @param tag Used to identify the source of a log message.  It usually identifies
	 *        the class or activity where the log call occurs.
	 * @param msg The message you would like logged.
	 * @return The number of bytes written.
	 */
	public static int println(int priority, String tag, String msg) {
		return println(LOG_ID_MAIN, priority, tag, msg);
	}

	public static final int LOG_ID_MAIN = 0;
	public static final int LOG_ID_RADIO = 1;
	public static final int LOG_ID_EVENTS = 2;
	public static final int LOG_ID_SYSTEM = 3;
	public static final int LOG_ID_CRASH = 4;

	private static boolean isOut = false;
	private static boolean setSystemOut = false;

	private static String mLogPath;
	private static AsyncOutputStreamHold mLogHold;

	//在不能输出时保存100条信息，待启用后一次性打印
	private static final int maxPreNumber = 100;
	//缓存enable()前的打印，仅支持Log::println(String)
	private static LinkedList<String> preMsgList = new LinkedList<>();

	private static PrintStream oldOut = System.out;
	private static PrintStream oldErr = System.err;

	public static void setSystemOut(boolean isSetSystemOut) {
		Log.setSystemOut = isSetSystemOut;

		if (isSetSystemOut) {
			Log.AsyncOutputStreamHold logHold = getLogHold();
			if (logHold == null) {
				return;
			}
			PrintStream mLog = logHold.getLog();
			if (mLog == null) {
				return;
			}
			System.setOut(mLog);
			System.setErr(mLog);
		} else {
			// 不替换系统流, 恢复
			System.setOut(Log.oldOut);
			System.setErr(Log.oldErr);
		}
	}

	public static void disable() {
		Log.isOut = false;
	}

	//启用
	public static void enable(String mNewLogPath) {
		Log.isOut = true;
		//过滤重复启用
		if (TextUtils.isEmpty(mNewLogPath)) {
			Log.isOut = false;
			return;
		}
		if (mNewLogPath.equals(getLogPath())) {
			return;
		}
		Log.mLogPath = mNewLogPath;
		// 更新写入路径
		updateCurLogHold();

		if (Log.getLog() != null) {
			//启用了，打印preMsgList
			printPreMsgList();
		}
	}

	private static void printPreMsgList() {
		while (!preMsgList.isEmpty()) {
			println(preMsgList.removeFirst());
		}
	}

	// 刷新流
	private static void updateCurLogHold() {
		Log.AsyncOutputStreamHold mLogHold = getLogHold();
		if (mLogHold == null) {
			mLogHold = new AsyncOutputStreamHold(Log.getLogPath(), true);
			// 更新 日志流持有者
			Log.mLogHold = mLogHold;
		} else {
			// 刷新路径
			mLogHold.update(Log.getLogPath());
		}

		//是否设置系统流
		if (Log.setSystemOut) {
			PrintStream mLog = mLogHold.getLog();
			if (mLog != null) {
				System.setOut(mLog);
				System.setErr(mLog);
			}
		}
	}

	/**
	 * 返回当前LogHold
	 */
	public static AsyncOutputStreamHold getLogHold() {
		return Log.mLogHold;
	}

	public static String getLogPath() {
		return mLogPath;
	}

	//
	public static PrintStream getLog() {
		Log.AsyncOutputStreamHold logHold = getLogHold();
		return logHold != null ? logHold.getLog() : null;
	}
	public static int println(int bufID, int priority, String tag, String msg) {
		if (!Log.isOut || mLogHold == null) {
			//未启用输出
			Log.println(ToString(priority, tag, msg));

			return 0;
		}
		if (Log.mLogHold == null) {
			Log.mLogHold = new AsyncOutputStreamHold(mLogPath, true);
		}
		//打印 Log为null则忽略
		Log.println(ToString(priority, tag, msg));
		return 1;
	}

	private static String ToString(int priority, String tag, String msg) {
		String timeString = getTimeString();
		String BlankSpace = "   ";
		String priorityString;
		switch (priority) {
			case INFO :
				priorityString = BlankSpace + "I";
				break;
			case VERBOSE :
				priorityString = BlankSpace + "V";
				break;
			case DEBUG :
				priorityString = BlankSpace + "D";
				break;
			case WARN :
				priorityString = BlankSpace + "W";
				break;
			case ERROR :
				priorityString = BlankSpace + "E";
				break;
			default :
				priorityString = "";
				//Log.getLog().println(priority);
				break;
		}

		timeString = timeString + priorityString + BlankSpace + tag + BlankSpace + msg;

		return timeString;
	}

	private static SimpleDateFormat formate;
	private static String getTimeString() {
		String timer = "";
		if (formate == null) {
			formate = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		}
		synchronized (formate) {
			Date date = new java.util.Date();
			timer = formate.format(date);
		}
		return timer;
	}

	//AsyncOutputStream持有者
	public static class AsyncOutputStreamHold implements AutoCloseable {
		private PrintStream mLog;
		private String logPath;
		boolean backup;
		//线程安全
		public AsyncOutputStreamHold(String filePath) {
			update(filePath);
		}
		public AsyncOutputStreamHold(String filePath, boolean backup) {
			this.backup = backup;
			update(filePath);
		}
		
		// 构造器中调用此方法
		// 因此设置为final
		public final synchronized void update(String newLogPath) {
			if (newLogPath == null) {
				return;
			}
			if (newLogPath.equals(this.logPath)) {
				return;
			}
			this.logPath = newLogPath;

			PrintStream lastLog = this.getLog();

			File logFile = new File(this.logPath);
			// 更新流
			Log.AsyncOutputStreamHold.AsyncOutStream asyncOutStream = new AsyncOutStream(createOutStream(logFile, this.backup));
			this.mLog = new PrintStream(asyncOutStream);

			// 如果上一个流是系统流则平滑的替换
			// lastLog是否是系统流
			// 如果是 跟随 lastLog 设置
			boolean isSetSystemStream = lastLog != null && lastLog == System.out && lastLog == System.err;
			if (this.mLog != null && isSetSystemStream) {
				// 说明是 setSystem
				System.setErr(this.mLog);
				System.setOut(this.mLog);
			}

			// 是否关闭流 不能是 Log.oldOut Log.oldErr 
			boolean closeLastLog = lastLog != null && lastLog != Log.oldOut && lastLog != Log.oldErr;
			// 关闭旧的流 
			if (closeLastLog) {
				// 关闭上一个流
				lastLog.close();
			}
		}

		public String getOutPath() {
			return this.logPath;
		}
		public PrintStream getLog() {
			return this.mLog;
		}

		@Override
		public void close() {
			if (this.getLog() != null) {
				//是否是系统流
				if (this.mLog != System.out && this.mLog != System.err) {
					mLog.close();
				}
			}
		}
		public static FileOutputStream createOutStream(String file) {
			return createOutStream(new File(file));
		}
		
		public static FileOutputStream createOutStream(File file) {
			return createOutStream(file, false);
		}
		public static FileOutputStream createOutStream(File file, boolean backup) {
			try {
				checkFile(file, backup);
				return new FileOutputStream(file);
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * checkFile 备份
		 */
		private static synchronized void checkFile(File file, boolean backup) {
			if (file.exists()) {
				// 备份旧的日志文件
				if( backup ){
					backupFile(file, 5);					
				}
				file.delete();
				return;
			}
			
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			} else if (parentFile.isFile()) {
				parentFile.delete();
				parentFile.mkdirs();
			}
		}
		/**
		 * 最大文件数量
		 * @param limitLogCount
		 */
		private static SimpleDateFormat backupFileFormate = new SimpleDateFormat("MM-dd-HH-mm-ss");
		private static void backupFile(File backupFile, int limitLogCount) {

			try {
				File parentFile = backupFile.getParentFile();
				
				// 备份目录 就是 备份文件的名称
				String backupFileName = backupFile.getName();
				String dirName = removeExtension(backupFileName);
				if (backupFileName.equals(dirName)) {
					dirName += "-dir";
				}
				File backupDir = new File(parentFile, dirName);
				if (!backupDir.exists()) {
					backupDir.mkdirs();
				}
				if (!backupDir.isDirectory()) {
					return;
				}
				String format = backupFileFormate.format(new Date(backupFile.lastModified()));
				format += ".txt";
				backupFile.renameTo(new File(backupDir, format));

				File[] files = backupDir.listFiles();
				if (files == null || files.length == 0) {
					return;
				}
				// 排序
				Arrays.sort(files);

				if (files.length <= limitLogCount) {
					return;
				}
				for (int i = 0; i < files.length - limitLogCount; i++) {
					files[i].delete();
				}
			} catch (Throwable e) {
				Log.e("AsyncOutputStreamHold", "backupFile", e);
			}
		}
		
		public static String removeExtension(String filename) {
			int dotIndex = filename.lastIndexOf('.');

			// 处理特殊情况：
			// 1. 没有后缀名（dotIndex == -1）
			// 2. 隐藏文件（dotIndex == 0）
			// 3. 文件名以点结尾（dotIndex == filename.length()-1）
			if (dotIndex == -1 || dotIndex == 0 || dotIndex == filename.length() - 1) {
				return filename;
			}

			return filename.substring(0, dotIndex);
		}
		
		public static class WriteLogThread extends Thread {
			ArrayBlockingQueue<Runnable> mQueue = new ArrayBlockingQueue<>(0x4000);
			@Override
			public void run() {
				while (true) {
					try {
						mQueue.take().run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}

		//异步写入 防止测试时影响性能,从而影响测试结果
		//异步的另一种实现
		public static class AsyncOutStream extends OutputStream {
			//AsynchronousFileChannel a = AsynchronousFileChannel.open(new File("").toPath(), StandardOpenOption.WRITE);
			private static WriteLogThread mWriteLogThread;
			protected OutputStream outputStream;
			protected OutputStream out;
			public AsyncOutStream(OutputStream outputStream) {
				this.out = outputStream;
				checkWriteLogThread();
			}
			//检查写入日志线程
			private void checkWriteLogThread() {
				if (mWriteLogThread == null) {
					mWriteLogThread = new WriteLogThread();
					mWriteLogThread.start();
				}
			}
			@Override
			public void flush() {
				addRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							out.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			@Override
			public void close() {
				addRunnable(new Runnable() {
					@Override
					public void run() {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			@Override
			public void write(final int b) throws IOException {
				Runnable mRunnable = new Runnable() {
					@Override
					public void run() {
						try {
							out.write(b);
						} catch (Throwable e) {
							e.printStackTrace(new PrintStream(out));
						}
					}
				};
				addRunnable(mRunnable);
			}

			@Override
			public void write(byte[] buf, final int off, final int len) {
				final byte[] buf2 = buf.clone();
				Runnable mRunnable = new Runnable() {
					@Override
					public void run() {
						try {
							out.write(buf2, off, len);
						} catch (IOException e) {
							e.printStackTrace(new PrintStream(out));
						}
					}
				};
				addRunnable(mRunnable);

			}

			private synchronized void addRunnable(Runnable mRunnable) {
				try {
					checkWriteLogThread();
					mWriteLogThread.mQueue.offer(mRunnable);
				} catch (Throwable e) {
					e.printStackTrace(new PrintStream(out));
				}
			}
		}

	}

}


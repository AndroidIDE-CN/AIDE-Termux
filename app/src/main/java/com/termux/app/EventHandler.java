/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package com.termux.app;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;
import java.io.File;
import java.util.Arrays;

public class EventHandler {
	
	
	//Gradle命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";
	
	// 退格 用于清空最新编辑行
	private static final byte[] backspaceKeyEventBytes = new byte[0x50];
	
	static{
		Arrays.fill(backspaceKeyEventBytes, (byte)0x8);
	}
	
	
	static Intent lastIntent;

	public static void distribute(Intent intent, TermuxActivity termuxActivity) {
		if (intent == null 
		//  过滤重复 intent
		|| intent.equals(EventHandler.lastIntent) 
		
		|| !intent.hasExtra(work_dir_extra)) {
			
			return;
		}
		
		lastIntent = intent;
		// termuxActivity.getTermuxTerminalViewClient();
		handlerGradleCmdLine(termuxActivity, intent);
	}

	private static void handlerGradleCmdLine(TermuxActivity termuxActivity, final Intent intent) {
		
		TerminalView terminalView = termuxActivity.getTerminalView();
		//接收传入的命令并异步运行
		terminalView.postDelayed(new Runnable() {
			@Override
			public void run() {

				String work_dir_text = intent.getStringExtra(work_dir_extra);
				String gradle_cmd_line_text = intent.getStringExtra(gradle_cmd_line_extra);
				
				intent.removeExtra(work_dir_extra);
				intent.removeExtra(gradle_cmd_line_extra);

				if (TextUtils.isEmpty(work_dir_text)) {
					return;
				}

				if (!new File(work_dir_text).exists()) {
					// 默认值
					work_dir_text = Environment.getExternalStorageDirectory().getAbsolutePath();
					//项目目录错误，不执行gradle
					gradle_cmd_line_text = null;
				}

				TerminalSession currentSession = terminalView.getCurrentSession();
				System.out.println( "currentSession " + currentSession);
				if (currentSession == null)
					return;
				
				// 清空 已有内容 0x100个字符
				currentSession.write(backspaceKeyEventBytes, 0, backspaceKeyEventBytes.length);
				
				if( !work_dir_text.equals(currentSession.getCwd())){
					//更改工作目录，防止有空格使用""包裹
					currentSession.write("cd \"".concat(work_dir_text).concat("\"\n"));					
				}

				if (TextUtils.isEmpty(gradle_cmd_line_text)) {
					return;
				}

				if (gradle_cmd_line_text == null) {
					return;
				}
				//处理gradle_cmd_line_text
				if (gradle_cmd_line_text.startsWith("gradle ")) {
					gradle_cmd_line_text = gradle_cmd_line_text.substring("gradle ".length());
				} else if (gradle_cmd_line_text.startsWith("gradlew ")) {
					gradle_cmd_line_text = gradle_cmd_line_text.substring("gradlew ".length());
				} else if (gradle_cmd_line_text.startsWith("./gradlew ")) {
					gradle_cmd_line_text = gradle_cmd_line_text.substring("./gradlew ".length());
				} else {
					//不是Gradle命令不执行
					return;
				}
				currentSession.write("$GRADLE " + gradle_cmd_line_text.concat("\n"));
			}
		}, 200);
	}
}


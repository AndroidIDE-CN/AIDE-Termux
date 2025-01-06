/**
 * @Author ZeroAicy
 * @AIDE AIDE+
*/
package io.github.zeroaicy.termux;
import android.app.Application;
import com.termux.app.TermuxApplication;
import io.github.zeroaicy.util.DebugUtil;
import io.github.zeroaicy.util.ContextUtil;

public class AIDETermuxApplication extends Application{

	@Override
	public void onCreate(){
		super.onCreate();
		ContextUtil.setApplicationContext(this.getApplicationContext());
		DebugUtil.debug(this);
		TermuxApplication.init(this);
	}
	
	
}

package a10h3y.com.testhotfix;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by guandongchen on 2017/7/13.
 */

public class App extends Application {

    private Context context;

    public interface MsgDisplayListener {
        void handle(String msg);
    }

    public static MsgDisplayListener msgDisplayListener = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        initHotfix();
        SophixManager.getInstance().queryAndLoadNewPatch();
    }


    private void initHotfix(){

        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0";
        }
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            Toast.makeText(context,"表明补丁加载成功",Toast.LENGTH_LONG).show();
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            Toast.makeText(context,"补丁生效需要重启. 开发者可提示用户或者强制重启",Toast.LENGTH_LONG).show();
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            Toast.makeText(context,"内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载",Toast.LENGTH_LONG).show();
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                            Toast.makeText(context,"内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载",Toast.LENGTH_LONG).show();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明


                        }

                        final String cacheMsg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();

                        msgDisplayListener.handle(cacheMsg);

                    }
                }).initialize();

    }
}

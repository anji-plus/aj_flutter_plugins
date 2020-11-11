package pedia.flutterumenganalytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterUmengAnalyticsPlugin
 */
public class FlutterUmengAnalyticsPlugin implements MethodCallHandler {
    private Activity activity;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel =
                new MethodChannel(registrar.messenger(), "flutter_umeng_analytics");
        channel.setMethodCallHandler(new FlutterUmengAnalyticsPlugin(registrar.activity()));
        registrar.activity().getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MobclickAgent.onResume(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MobclickAgent.onPause(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private FlutterUmengAnalyticsPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("init")) {
            init(call, result);
        } else if (call.method.equals("beginPageView")) {
            MobclickAgent.onPageStart((String) call.argument("name"));
            result.success(null);
        } else if (call.method.equals("endPageView")) {
            MobclickAgent.onPageEnd((String) call.argument("name"));
            result.success(null);
        } else if (call.method.equals("logEvent")) {
            if (call.argument("label") == null) {
                MobclickAgent.onEvent((Context) activity, (String) call.argument("name"));
            } else {
                MobclickAgent.onEvent((Context) activity, (String) call.argument("name"), (String) call.argument("label"));
            }
            result.success(null);
        } else if (call.method.equals("reportError")) {
            MobclickAgent.reportError((Context) activity, (String) call.argument("name"));
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    public void init(MethodCall call, Result result) {
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(activity.getApplication(), (String) call.argument("key"), "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setEncryptEnabled(true);
        result.success(true);
    }
}

package com.anjiplus.flutter_x5webview_plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * FlutterX5webviewPlugin
 */
public class FlutterX5webviewPlugin implements FlutterPlugin, MethodCallHandler, PluginRegistry.ActivityResultListener, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    static MethodChannel channel;
    private Activity activity;
    private WebviewManager webViewManager;
    private static final String JS_CHANNEL_NAMES_FIELD = "javascriptChannelNames";
    private Handler platformThreadHandler;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
       
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_x5webview_plugin");
        channel.setMethodCallHandler(this);

        Log.e("webview","onAttachedToEngine");
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "init":
                //x5webview 设置开启优化方案
                HashMap map = new HashMap();
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
                QbSdk.initTbsSettings(map);
                QbSdk.initX5Environment(activity,new QbSdk.PreInitCallback() {
                    @Override
                    public void onCoreInitFinished() {

                    }

                    @Override
                    public void onViewInitFinished(boolean b) {
                        Log.e("webview","init"+b);
                    }
                });
                //

                break;
			case "getPlatformVersion":
			    result.success("Android " + android.os.Build.VERSION.RELEASE);
			    break;
            case "launch":
                openUrl(call, result);
                break;
            case "close":
                close(call, result);
                break;
            case "eval":
                eval(call, result);
                break;
            case "resize":
                resize(call, result);
                break;
            case "reload":
                reload(call, result);
                break;
            case "back":
                back(call, result);
                break;
            case "forward":
                forward(call, result);
                break;
            case "canGoBack":
                canGoBack(call, result);
                break;
            case "canGoForward":
                canGoForward(call, result);
                break;
            case "hide":
                hide(call, result);
                break;
            case "show":
                show(call, result);
                break;
            case "reloadUrl":
                reloadUrl(call, result);
                break;
            case "stopLoading":
                stopLoading(call, result);
                break;
            case "cleanCookies":
                cleanCookies(call, result);
                break;
            case "setDownloadWithoutWifi":
                boolean isWithoutWifi=call.argument("isDownloadWithoutWifi");
                QbSdk.setDownloadWithoutWifi(isWithoutWifi);
                result.success(null);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void openUrl(MethodCall call, MethodChannel.Result result) {
        boolean hidden = call.argument("hidden");
        String url = call.argument("url");
        String userAgent = call.argument("userAgent");
        boolean withJavascript = call.argument("withJavascript");
        boolean clearCache = call.argument("clearCache");
        boolean clearCookies = call.argument("clearCookies");
        boolean withZoom = call.argument("withZoom");
        boolean withLocalStorage = call.argument("withLocalStorage");
        boolean supportMultipleWindows = call.argument("supportMultipleWindows");
        boolean appCacheEnabled = call.argument("appCacheEnabled");
        Map<String, String> headers = call.argument("headers");
        boolean scrollBar = call.argument("scrollBar");
        boolean allowFileURLs = call.argument("allowFileURLs");
        boolean useWideViewPort = call.argument("useWideViewPort");
        String invalidUrlRegex = call.argument("invalidUrlRegex");
        boolean geolocationEnabled = call.argument("geolocationEnabled");

        if (webViewManager == null || webViewManager.closed == true) {
            webViewManager = new WebviewManager(activity, activity);
        }

        FrameLayout.LayoutParams params = buildLayoutParams(call);

        activity.addContentView(webViewManager.webView, params);
        platformThreadHandler = new Handler(activity.getMainLooper());

        if (call.argument(JS_CHANNEL_NAMES_FIELD) instanceof List) {
            registerJavaScriptChannelNames((List<String>) call.argument(JS_CHANNEL_NAMES_FIELD));
        }
        webViewManager.openUrl(withJavascript,
                clearCache,
                hidden,
                clearCookies,
                userAgent,
                url,
                headers,
                withZoom,
                withLocalStorage,
                scrollBar,
                supportMultipleWindows,
                appCacheEnabled,
                allowFileURLs,
                useWideViewPort,
                invalidUrlRegex,
                geolocationEnabled
        );
        result.success(null);
    }

    private FrameLayout.LayoutParams buildLayoutParams(MethodCall call) {
        Map<String, Number> rc = call.argument("rect");
        FrameLayout.LayoutParams params;
        if (rc != null) {
            params = new FrameLayout.LayoutParams(
                    dp2px(activity, rc.get("width").intValue()), dp2px(activity, rc.get("height").intValue()));
            params.setMargins(dp2px(activity, rc.get("left").intValue()), dp2px(activity, rc.get("top").intValue()),
                    0, 0);
        } else {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            params = new FrameLayout.LayoutParams(width, height);
        }

        return params;
    }

    private void stopLoading(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.stopLoading(call, result);
        }
        result.success(null);
    }

    private void close(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.close(call, result);
            webViewManager = null;
        }
    }

    /**
     * Navigates back on the Webview.
     */
    private void back(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.back(call, result);
        }
        result.success(null);
    }

    /**
     * Navigates forward on the Webview.
     */
    private void forward(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.forward(call, result);
        }
        result.success(null);
    }

    /**
     * Navigates canGoBack on the Webview.
     */
    private void canGoBack(MethodCall call, MethodChannel.Result result) {
        boolean canBack = false;
        if (webViewManager != null) {
            canBack = webViewManager.canGoBack();
        }
        result.success(canBack);
    }

    /**
     * Navigates canGoForward on the Webview.
     */
    private void canGoForward(MethodCall call, MethodChannel.Result result) {
        boolean canForward = false;
        if (webViewManager != null) {
            canForward = webViewManager.canGoForward();
        }
        result.success(canForward);
    }

    /**
     * Reloads the Webview.
     */
    private void reload(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.reload(call, result);
        }
        result.success(null);
    }

    private void reloadUrl(MethodCall call, MethodChannel.Result result) {
        if (webViewManager != null) {
            String url = call.argument("url");
            webViewManager.reloadUrl(url);
        }
        result.success(null);
    }

    private void eval(MethodCall call, final MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.eval(call, result);
        }
    }

    private void resize(MethodCall call, final MethodChannel.Result result) {
        if (webViewManager != null) {
            FrameLayout.LayoutParams params = buildLayoutParams(call);
            webViewManager.resize(params);
        }
        result.success(null);
    }

    private void hide(MethodCall call, final MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.hide(call, result);
        }
        result.success(null);
    }

    private void show(MethodCall call, final MethodChannel.Result result) {
        if (webViewManager != null) {
            webViewManager.show(call, result);
        }
        result.success(null);
    }

    private void cleanCookies(MethodCall call, final MethodChannel.Result result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {

                }
            });
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
        result.success(null);
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (webViewManager != null && webViewManager.resultHandler != null) {
            return webViewManager.resultHandler.handleResult(requestCode, resultCode, intent);
        }
        return false;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        binding.addActivityResultListener(this);
        this.activity = binding.getActivity();
    }

    private void registerJavaScriptChannelNames(List<String> channelNames) {
        for (String channelName : channelNames) {
            webViewManager.webView.addJavascriptInterface(
                    new JavaScriptChannel(channel, channelName, platformThreadHandler), channelName);
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}

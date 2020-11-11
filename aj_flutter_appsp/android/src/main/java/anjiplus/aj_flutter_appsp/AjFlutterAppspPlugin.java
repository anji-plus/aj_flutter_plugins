package anjiplus.aj_flutter_appsp;

import android.os.Handler;
import android.os.Looper;

import com.anji.appsp.sdk.AppSpConfig;
import com.anji.appsp.sdk.AppSpLog;
import com.anji.appsp.sdk.IAppSpNoticeCallback;
import com.anji.appsp.sdk.IAppSpVersionUpdateCallback;
import com.anji.appsp.sdk.model.AppSpModel;
import com.anji.appsp.sdk.model.AppSpNoticeModelItem;
import com.anji.appsp.sdk.model.AppSpVersion;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * AjFlutterAppSpPlugin
 */
public class AjFlutterAppspPlugin implements MethodCallHandler {
    private static Registrar registrar;
    //为了解决并发问题，比如多次点击，异常情况时候容易出问题
    private ConcurrentLinkedQueue<MethodResultWrapper> wrappers = new ConcurrentLinkedQueue<>();

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        AjFlutterAppspPlugin.registrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "aj_flutter_appsp");
        channel.setMethodCallHandler(new AjFlutterAppspPlugin());
    }

    // MethodChannel.Result wrapper that responds on the platform thread.
    private static class MethodResultWrapper implements MethodChannel.Result {
        private MethodChannel.Result methodResult;
        private Handler handler;

        MethodResultWrapper(MethodChannel.Result result) {
            methodResult = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object result) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.success(result);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            AppSpLog.d("Test error  ");
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.notImplemented();
                        }
                    });
        }
    }

    private void checkVersion(String appKey) {
        AppSpConfig.getInstance().init(registrar.activity(), appKey);
        AppSpConfig.getInstance().setVersionUpdateCallback(new IAppSpVersionUpdateCallback() {
            @Override
            public void update(AppSpModel<AppSpVersion> spModel) {
                AppSpLog.d("Test updateModel is " + spModel);
                MethodResultWrapper wrapper = peekWraper();
                if (spModel == null) {
                    if (wrapper != null) {
                        wrapper.notImplemented();
                    }
                } else {
                    //先转成json
                    if (spModel.getRepData() != null) {
                        if (wrapper != null) {
                            wrapper.success(new Gson().toJson(spModel));
                        }
                    } else {
                        AppSpModel tempModel = new AppSpModel<>();
                        tempModel.setRepCode(spModel.getRepCode());
                        tempModel.setRepMsg(spModel.getRepMsg());
                        if (wrapper != null) {
                            wrapper.success(new Gson().toJson(tempModel));
                        }
                    }
                }

            }

            @Override
            public void error(String code, String msg) {
                MethodResultWrapper wrapper = peekWraper();
                AppSpModel spModel = new AppSpModel<>();
                spModel.setRepCode(code);
                spModel.setRepMsg(msg);
                if (wrapper != null) {
                    wrapper.success(new Gson().toJson(spModel));
                }
            }
        });
    }

    private void checkNotice(String appKey) {
        AppSpConfig.getInstance().init(registrar.activity(), appKey);
        AppSpConfig.getInstance().setNoticeCallback(new IAppSpNoticeCallback() {
            @Override
            public void notice(AppSpModel<List<AppSpNoticeModelItem>> noticeModel) {
                AppSpLog.d("Test noticeModel is " + noticeModel);
                MethodResultWrapper wrapper = peekWraper();
                if (noticeModel == null) {
                    if (wrapper != null) {
                        wrapper.notImplemented();
                    }
                } else if (noticeModel.getRepData() != null) {
                    if (wrapper != null) {
                        wrapper.success(new Gson().toJson(noticeModel));
                    }
                } else {
                    //先转成json
                    AppSpModel tempModel = new AppSpModel<>();
                    tempModel.setRepCode(noticeModel.getRepCode());
                    tempModel.setRepMsg(noticeModel.getRepMsg());
                    if (wrapper != null) {
                        wrapper.success(new Gson().toJson(tempModel));
                    }
                }
            }

            @Override
            public void error(String code, String msg) {
                AppSpModel noticeModel = new AppSpModel<>();
                noticeModel.setRepCode(code);
                noticeModel.setRepMsg(msg);
                MethodResultWrapper wrapper = peekWraper();
                if (wrapper != null) {
                    wrapper.success(new Gson().toJson(noticeModel));
                }
            }
        });
    }

    /**
     * 获取当前的result
     * @return
     */
    private MethodResultWrapper peekWraper() {
        if (wrappers == null
                || wrappers.isEmpty()) {
            return null;
        }
        return wrappers.remove();
    }

    /**
     * 只考虑最后一次
     */
    private void removeAllWrapper() {
        if (wrappers == null) {
            return;
        }
        wrappers.clear();
    }

    /**
     * 加入唯一的result
     * @param wrapper
     */
    private void addWraper(MethodResultWrapper wrapper) {
        if (wrappers == null) {
            return;
        }
        wrappers.add(wrapper);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        removeAllWrapper();
        addWraper(new MethodResultWrapper(result));
        if (call.method.equals("getUpdateModel")) {
            String appkey = null;
            Object parameter = call.arguments();

            if (parameter instanceof Map) {
                appkey = (String) ((Map) parameter).get("appKey");
                checkVersion(appkey);
            }
        } else if (call.method.equals("getNoticeModel")) {
            String appkey = null;
            Object parameter = call.arguments();

            if (parameter instanceof Map) {
                appkey = (String) ((Map) parameter).get("appKey");
                checkNotice(appkey);
            }
        } else {
            MethodResultWrapper wrapper = peekWraper();
            if (wrapper != null) {
                wrapper.notImplemented();
            }
        }
    }
}

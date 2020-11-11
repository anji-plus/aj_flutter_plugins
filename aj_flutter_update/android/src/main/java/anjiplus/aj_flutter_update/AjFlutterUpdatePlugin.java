package anjiplus.aj_flutter_update;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * AjFlutterUpdatePlugin
 */
public class AjFlutterUpdatePlugin implements MethodCallHandler {
    private static Registrar registrar;

    /**
     * Plugin registration.
     */
    public static void registerWith(final Registrar registrar) {
        AjFlutterUpdatePlugin.registrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "aj_flutter_update");
        channel.setMethodCallHandler(new AjFlutterUpdatePlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("apkinstallMethod")) {
            Object parameter = call.arguments();
            if (parameter instanceof Map) {
                String value = (String) ((Map) parameter).get("path");
                boolean installAllowed = true;
                if (Build.VERSION.SDK_INT >= 26) {
                    //来判断应用是否有权限安装apk
                    installAllowed = registrar.activity().getPackageManager().canRequestPackageInstalls();
                    //有权限
                    if (!installAllowed) {
                        //无权限 申请权限
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + registrar.activity().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        registrar.activity().startActivity(intent);
                        VersionUpdateInstaller.installApk(registrar.activity().getApplication(), value);
                        return;
                    }
                }
                if (installAllowed) {
                    VersionUpdateInstaller.installApk(registrar.activity().getApplication(), value);
                }
            }
            //版本更新
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else {
            result.notImplemented();
        }
    }
}

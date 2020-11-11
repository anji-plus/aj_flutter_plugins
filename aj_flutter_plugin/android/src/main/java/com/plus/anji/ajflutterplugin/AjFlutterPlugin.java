package com.plus.anji.ajflutterplugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebStorage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.provider.Settings;
import static android.content.ContentValues.TAG;

/** AjFlutterPlugin */

public class AjFlutterPlugin implements MethodCallHandler {
  private final Registrar mRegistrar;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "aj_flutter_plugin");
    channel.setMethodCallHandler(new AjFlutterPlugin(registrar));
  }

  private AjFlutterPlugin(Registrar registrar){
    this.mRegistrar = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    try {
      Context context = mRegistrar.context();
      if (call.method.equals("getPlatformVersion")) { //获取版本信息
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
        Map<String, String> map = new HashMap<String, String>();
        map.put("appName", info.applicationInfo.loadLabel(pm).toString());
        map.put("packageName", context.getPackageName());
        map.put("version", info.versionName);
        map.put("buildNumber", String.valueOf(info.versionCode));
        map.put("deviceId", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        result.success(map);
      } else if (call.method.equals("launchUrl")){ //跳转外链，打电话，发邮件
        String url = call.argument("url");
        Intent launchIntent;
        if (mRegistrar.activity() != null) {
          context = (Context) mRegistrar.activity();
        } else {
          context = mRegistrar.context();
        }

        launchIntent = new Intent(Intent.ACTION_VIEW);
        launchIntent.setData(Uri.parse(url));
        if (mRegistrar.activity() == null) {
          launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(launchIntent);

        result.success(null);

      } else if (call.method.equals("canLaunch")) { //是否可以跳转
        String url = call.argument("url");
        canLaunch(url, result);
      } else if (call.method.equals("selfStart")) { //是否可以跳转
        selfStart(call, result);
      } else if (call.method.equals("clearWebCache")) { //清除缓存
        result.success(clearWebCache());
      } else if (call.method.equals("exitAppMethod")) { //回到桌面
        if (mRegistrar.activity() != null) {
          context = (Context) mRegistrar.activity();
        } else {
          context = mRegistrar.context();
        }

        if (context instanceof Activity) {
          System.out.println("退出");
          ((Activity)context).moveTaskToBack(true);
        }
        result.success(null);

      } else {
        result.notImplemented();
      }
    } catch (PackageManager.NameNotFoundException e){
      result.error("Not found", e.getMessage(), null);
    }
  }


  private boolean canLaunchUrl(String url){
    Intent launchIntent = new Intent(Intent.ACTION_VIEW);
    launchIntent.setData(Uri.parse(url));
    ComponentName componentName =
            launchIntent.resolveActivity(mRegistrar.context().getPackageManager());

    boolean canLaunch =
            componentName != null
                    && !"{com.android.fallback/com.android.fallback.Fallback}"
                    .equals(componentName.toShortString());
    return  canLaunch;
  }


  private void canLaunch(String url, Result result) {
    boolean canLaunch = this.canLaunchUrl(url);
    result.success(canLaunch);
  }

  private void selfStart(MethodCall call, Result result)  {
    try {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      ComponentName cn =
              intent.resolveActivity(mRegistrar.context().getPackageManager());
      intent.setComponent(cn);
      mRegistrar.context().startActivity(intent);
      result.success(null);
    } catch (Exception e){
      result.error("Not found", e.getMessage(), null);
    }
  }
  private boolean clearWebCache(){
    WebStorage.getInstance().deleteAllData();
    return true;
  }



//  /**
//   * 清除WebView缓存
//   */
//  private void clearWebViewCache(){
//    Context context = new Activity();
//
//    //清理Webview缓存数据库
//    try {
//      context.deleteDatabase("webview.db");
//      context.deleteDatabase("webviewCache.db");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    //WebView 缓存文件
//    File appCacheDir = new File(context.getFilesDir().getAbsolutePath()+"/webcache");
//
//    File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()+"/webviewCache");
//
//    //删除webview 缓存目录
//    if(webviewCacheDir.exists()){
//      deleteFile(webviewCacheDir);
//    }
//    //删除webview 缓存 缓存目录
//    if(appCacheDir.exists()){
//      deleteFile(appCacheDir);
//    }
//  }
//
//  /**
//   * 递归删除 文件/文件夹
//   *
//   * @param file
//   */
//  private void deleteFile(File file) {
//
//    Log.i(TAG, "delete file path=" + file.getAbsolutePath());
//
//    if (file.exists()) {
//      if (file.isFile()) {
//        file.delete();
//      } else if (file.isDirectory()) {
//        File files[] = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//          deleteFile(files[i]);
//        }
//      }
//      file.delete();
//    } else {
//      Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
//    }
//  }

}



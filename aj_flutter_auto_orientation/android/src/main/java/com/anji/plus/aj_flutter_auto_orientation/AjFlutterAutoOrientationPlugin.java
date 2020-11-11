package com.anji.plus.aj_flutter_auto_orientation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AjFlutterAutoOrientationPlugin */
public class AjFlutterAutoOrientationPlugin implements MethodCallHandler {
  Activity activity;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "aj_flutter_auto_orientation");
    channel.setMethodCallHandler(new AjFlutterAutoOrientationPlugin(registrar.activity()));
  }
  private AjFlutterAutoOrientationPlugin(Activity activity) {
    this.activity = activity;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch(call.method) {
      case "setLandscapeRight":
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        break;
      case "setLandscapeLeft":
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        break;
      case "setPortraitUp":
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        break;
      case "setPortraitDown":
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        break;
      default:
        result.notImplemented();
        break;
    }
  }
}

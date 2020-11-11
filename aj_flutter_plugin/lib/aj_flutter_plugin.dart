import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';


const MethodChannel _channel = const MethodChannel('aj_flutter_plugin');

/// 跳转外部浏览器、打电话、发邮件
/// 外链url:  http:xxx, https: -----------test
/// 与canLaunch同步使用
Future<void> launch(String urlString) async{
  if(urlString.isNotEmpty){
    await _channel.invokeMethod('launchUrl', {'url': urlString});
  }
}

//打电话
Future<void> launchCallPhone(String phoneNum)async{
  if(phoneNum.isNotEmpty){
  String _callphone = "tel:" + phoneNum;
  await _channel.invokeMethod('launchUrl', {'url': _callphone});
  }
}

//发短信
Future<void> launchMessage(String messagePhone)async{
  if(messagePhone.isNotEmpty){
    String _messagePhone = "sms:" + messagePhone;
    await _channel.invokeMethod('launchUrl', {'url': _messagePhone});
  }
}

//发邮箱 手机号
Future<void> launchEmail(String emailNum) async{
  if(emailNum.isNotEmpty){
    String _emailNum = "mailto:" + emailNum;
    await _channel.invokeMethod('launchUrl', {'url': _emailNum});
  }
}

//退出APP
Future<void> exitApp()async{
  await _channel.invokeMethod('exitAppMethod');
}

//是否可以跳转
Future<bool> canLaunch(String urlString) async {
  if (urlString == null) {
    return false;
  }
  return await _channel.invokeMethod(
    'canLaunch',
    <String, Object>{'url': urlString},
  );
}

//判断是否是iOS模拟器
Future<bool> isiOSSimuLator() async {
  if(Platform.isIOS){
    return await _channel.invokeMethod('isiOSSimuLator');
  } else{
    return false;
  }

}

class AjFlutterPlugin {

  AjFlutterPlugin({
    this.appName,
    this.packageName,
    this.version,
    this.buildNumber,
    this.deviceId
  });
  static Future<AjFlutterPlugin> _fromPlatform;

  //判断iOS请求权限
  static Future<int> getLocationPermissions() async {
    if(Platform.isIOS){
      return await _channel.invokeMethod('locationPermissions');
    } else{
      return 1;
    }

  }

  //Android 自启动
  static Future<void> getSelfStart() async {
    if(Platform.isAndroid){
      await _channel.invokeMethod('selfStart');
    }
  }
//  //iOS获取权限
//  static Future<int> getRequestlocationAuthorization() async {
//    if(Platform.isIOS){
//      return await _channel.invokeMethod('requestlocationAuthorization');
//    } else{
//      return 1;
//    }
//
//  }



  ///获取版本信息
  static Future<AjFlutterPlugin> platformVersion() async {
    if(_fromPlatform == null){
      final Completer<AjFlutterPlugin> completer = Completer<AjFlutterPlugin>();
      _channel.invokeMethod("getPlatformVersion").then((dynamic result){
        final Map<dynamic, dynamic> map = result;
        completer.complete(AjFlutterPlugin(
            appName: map["appName"],
            packageName: map["packageName"],
            version: map["version"],
            buildNumber: map["buildNumber"],
            deviceId: map["deviceId"]?? ""
        ));
      }, onError: completer.completeError);

      _fromPlatform = completer.future;
    }

    return _fromPlatform;
  }


  /// The app name. `CFBundleDisplayName` on iOS, `application/label` on Android.
  final String appName;

  /// The package name. `bundleIdentifier` on iOS, `getPackageName` on Android.
  final String packageName;

  /// The package version. `CFBundleShortVersionString` on iOS, `versionName` on Android.
  final String version;

  /// The build number. `CFBundleVersion` on iOS, `versionCode` on Android.
  final String buildNumber;
  //设备ID
  final String deviceId;

  static Future<bool> clearWebCache() async {
    return await _channel.invokeMethod('clearWebCache');
  }
}

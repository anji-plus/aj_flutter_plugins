import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'Commons.dart';
import 'aj_flutter_update.dart';

class AppUtils {
  static List<String> getMsgList(String releaseLog, {String split}) {
    List<String> list = [];
    if (releaseLog != null) {
      list = releaseLog.split('==');
    }
    return list;
  }

  /*
  * appurl 跳转URL
  * */
  static Future gotoAppstore(BuildContext context, String appurl) async {
    Navigator.pop(context);
    AjFlutterUpdateMixin.apkInstallChannel.invokeMethod(Commons.iOSInstallMethod, {'url': appurl});
  }

  static Future push(BuildContext context, Widget page,
      {replace = false, reverse = false, needWait = false}) {
    if (replace) {
      return Navigator.pushAndRemoveUntil(context,
          PageRouteBuilder(pageBuilder: (context, _, __) {
        return page;
      }), (route) {
        return !route.isActive;
      });
    }

    return Navigator.push(
        context, MaterialPageRoute(builder: (context) => page));
  }

  static Future pushName(BuildContext context, String routeName,
      {replace = false, reverse = false, needWait = false}) {
    return Navigator.pushNamed(context, routeName);
  }

  static SlideTransition createTransition(
      Animation<double> animation, Widget child, bool reverse) {
    double offset = reverse ? -1.0 : 1.0;
    return new SlideTransition(
      position: new Tween<Offset>(
        begin: Offset(offset, 0.0),
        end: Offset(0.0, 0.0),
      ).animate(animation),
      child: child, // child is the value returned by pageBuilder
    );
  }

  //退出APP
  static Future<void> popApp() async {
    await SystemChannels.platform.invokeMethod('SystemNavigator.pop');
  }
}

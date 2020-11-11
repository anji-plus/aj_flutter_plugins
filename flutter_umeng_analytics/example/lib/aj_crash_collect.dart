import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_umeng_analytics/flutter_umeng_analytics.dart';

import 'aj_log_util.dart';

class AJCrashCollect {
  static void init<R>(R body()) {
    FlutterError.onError = (FlutterErrorDetails details) {
      //TODO 上报错误和日志逻辑
      _reportErrorAndLog(details);
    };
    runZoned(
      () => body(),
//      zoneSpecification: ZoneSpecification(
//        print: (Zone self, ZoneDelegate parent, Zone zone, String line) {
//          _collectLog(line); // TODO 收集日志
//        },
//      ),
      onError: (Object obj, StackTrace stack) {
        // TODO 构建错误信息
        var details = _makeDetails(obj, stack);
        //TODO 上报错误和日志逻辑
        _reportErrorAndLog(details);
      },
    );
  }

  static void _collectLog(String line) {
    //TODO 收集日志
    print("_collectLog");
//    print(line);
  }

  static void _reportErrorAndLog(FlutterErrorDetails details) {
    //TODO 上报错误和日志逻辑
//    print(details.toString());
    AJLogUtil.v(details.toString(), tag: "_reportErrorAndLog -------->");
  }

  static FlutterErrorDetails _makeDetails(Object obj, StackTrace stack) {
    // TODO 构建错误信息
    AJLogUtil.v("$obj", tag: "_makeDetails_obj");
    AJLogUtil.v(stack.toString(), tag: "_makeDetails_stack -------- >");
    FlutterErrorDetails details = FlutterErrorDetails(stack: stack);
    UMengAnalytics.reportError("details：" + details?.toString() ?? "" +"\n stack:"+ stack.toString());
  }
}

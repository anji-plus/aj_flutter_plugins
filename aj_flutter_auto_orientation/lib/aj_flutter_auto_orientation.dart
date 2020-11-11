import 'package:flutter/services.dart';

class AjFlutterAutoOrientation {
  static const MethodChannel _channel =
      const MethodChannel('aj_flutter_auto_orientation');

  // rotate the device to landscape left mode
  ////屏幕方向向右
  static landscapeLeftMode() async {
    try {
      await _channel.invokeMethod('setLandscapeLeft');
    } on MissingPluginException catch (_) {
      return;
    }
  }

  // rotate the device to landscape right mode
  //屏幕方向向左
  static landscapeRightMode() async {
    try {
      await _channel.invokeMethod('setLandscapeRight');
    } on MissingPluginException catch (_) {
      return;
    }
  }

  // rotate the device to portrait up mode
  //屏幕方向向下
  static portraitUpMode() async {
    try {
      await _channel.invokeMethod('setPortraitUp');
    } on MissingPluginException catch (_) {
      return;
    }
  }

  // rotate the device to portrait down mode
  //屏幕方向向上
  static portraitDownMode() async {
    try {
      await _channel.invokeMethod('setPortraitDown');
    } on MissingPluginException catch (_) {
      return;
    }
  }
}

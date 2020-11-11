import 'package:flutter/material.dart';

class AJLogUtil {
  static const String _TAG_DEF = "###AJLogUtil###";
  static int _strLength  = 1000;
  static bool debuggable = true; //是否是debug模式,true: log v 不输出.
  static String TAG = _TAG_DEF;

  static void init({bool isDebug = false, String tag = _TAG_DEF,int strLength = 1000}) {
    debuggable = !bool.fromEnvironment("dart.vm.product");
//    debuggable = isDebug;
    _strLength = strLength;
    TAG = tag;
  }

  static void e(Object object, {String tag}) {
    _printLog(tag, '  e  ', object);
  }

  static void v(Object object, {String tag}) {
    if (debuggable) {
      _printLog(tag, '  v  ', object);
    }
  }

  static void _printLog(String tag, String stag, Object object) {
    String nowTime = _getNowTime();
    String str = "${nowTime} ${(tag == null || tag.isEmpty) ? TAG : tag} $stag ${object.toString()}";
    debugPrint("str.length ${str.length}", wrapWidth: _strLength);
    if(str.length > 10000){
      debugPrint(str.substring(0,10000) + "...");
    } else {
      debugPrint(str, wrapWidth: _strLength);
    }

//
//    if(object.toString().length > _strLength){
//      StringBuffer sb = _stringBuffer(tag, stag, object);
//      _stringInterception(sb);
//    } else {
//      StringBuffer sb = _stringBuffer(tag, stag, object);
//      debugPrint(sb.toString());
//    }

  }

  static void _stringInterception(Object object){
    String str = object.toString();
    String str1 = str.substring(0, _strLength);
    String str2 = str.substring(_strLength);
    if(str2.length > _strLength){
      debugPrint(str1);
      _stringInterception(str2);
    } else {
      debugPrint(str1);
      debugPrint(str2);
    }
  }

  /// 转化 StringBuffer
  static StringBuffer _stringBuffer(String tag, String stag, Object object) {
    StringBuffer sb = new StringBuffer();
    sb.write(_getNowTime());
    sb.write((tag == null || tag.isEmpty) ? TAG : tag);
    sb.write(stag);
    sb.write(object);
    return sb;
  }

  static String _getNowTime() {
    var date = new DateTime.now();
    String hour = date.hour.toString().padLeft(2, '0');
    String minute = date.minute.toString().padLeft(2, '0');
    String second = date.second.toString().padLeft(2, '0');
    String millisecond = date.millisecond.toString().padLeft(2, '0');
    String timestamp = "${hour}:${minute}:${second}:${millisecond} ";
    return timestamp ?? '';
  }
}

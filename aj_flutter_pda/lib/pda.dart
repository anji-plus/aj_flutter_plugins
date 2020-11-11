import 'dart:async';

import 'package:flutter/services.dart';
import 'commons.dart';

/*
@author wuyan
Date:2019/10/21
功能：PDA扫描、打印、读取车架号
 */
class Pda {
  /*
  {
        "marginLeft": 10, //x轴位置
        "marginRight": 10,
        "gravity" : 1 // 居1左、2、居中 3、居右
        "barWidth": 100, //条形码或二维码宽度
        "barHeight": 100, //条形码或二维码高度
        "bold": true, //是否粗体
        "underLine":true, //是否下划线
        "fontSize": 14, //字体
        "title": "LSAFASFASF",//标题
        "byteMapPath": "XXXX", //图片路径，默认空
        "mode": 1 //1、普通的text 2、条形码 3、二维码 4、图片
      },
  * */
  static const MethodChannel _channel =
      const MethodChannel(Commons.pdaMethodChannel);

  ///获取条形码或二维码的扫描结果
  static Future<String> startScan() async {
    final String result = await _channel.invokeMethod(Commons.startScanMethod);
    return result;
  }

  ///停止扫描
  static stopScan() {
    _channel.invokeMethod(Commons.stopScanMethod);
  }

  ///打印
  static print(Map map) {
    _channel.invokeMethod(Commons.printMethod, map);
  }

  ///走纸一张（防止位置不准）
  static goNextPage() {
    _channel.invokeMethod(Commons.goNextPageMethod);
  }

  ///读取车架号
  static Future<String> readRFIDCode({bool isNeedDialog}) async {
    final rfidCode =
        await _channel.invokeMethod(Commons.readRFIDCodeMethod, isNeedDialog);
    return rfidCode;
  }

  ///制卡
  static Future<int> WriteRFIDCode(String vin, {bool isNeedDialog}) async {
    var map = {
      "isNeedDialog": isNeedDialog,
      "vin": vin,
    };
    final result =
        await _channel.invokeMethod(Commons.writeRFIDCodeMethod, map);
    return result;
  }

  ///判断是否是pda
  static Future<bool> isPda() async {
    final bool result = await _channel.invokeMethod(Commons.isPDAMethod);
    return result;
  }
}

import 'aj_flutter_appsp_lib.dart';

import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

class AjFlutterAppSp {
  static const MethodChannel _channel = const MethodChannel('aj_flutter_appsp');

  static Future<SpRespUpdateModel> getUpdateModel({String appKey}) async {
    final String jsonStr =
        await _channel.invokeMethod('getUpdateModel', {"appKey": appKey});
    SpRespUpdateModel updateModel =
        SpRespUpdateModel.fromJson(json.decode(jsonStr));
    return updateModel;
  }

  static Future<SpRespNoticeModel> getNoticeModel({String appKey}) async {
    final String jsonStr =
        await _channel.invokeMethod('getNoticeModel', {"appKey": appKey});
    SpRespNoticeModel noticeModel =
        SpRespNoticeModel.fromJson(json.decode(jsonStr));
    return noticeModel;
  }
}

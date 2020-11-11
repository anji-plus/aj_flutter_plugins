import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

import 'AppUtils.dart';
import 'Commons.dart';
import 'DialogUtils.dart';
import 'VersionUpdateDialog.dart';

mixin AjFlutterUpdateMixin<T extends StatefulWidget> on State<T> {
  static const apkInstallChannel =
      const MethodChannel(Commons.apkinstallChannel);

  static const String apkInstallMethod = Commons.apkInstallMethod;

  //先接口请求，versionName（code）比对后再调用，这个请求+比对放在本地即可
  /// [downloadUrl] 下载地址 （iOS 跳转App Store URL 或者 跳转下载页 URL，Android下载apkURL ）
  /// [updateLog] 更新信息描述 以 == 做分割
  /// [mustUpdate] 是否强制更新 default is false
  /// [mustUpdate] 是否强制更新 default is false
  /// [onCancelTap] 取消回调
  /// [buttonColor] 确认回调
  /// [titleColor] 标题颜色
  static versionUpdate(
      {BuildContext context,
      String downloadUrl,
      String updateLog,
      bool mustUpdate = false,
      Color buttonColor = Colors.blue,
      GestureTapCallback onConformTap,
      GestureTapCallback onCancelTap,
      Color titleColor = const Color(0xFFFFA033)}) async {
    if (Platform.isIOS) {
      showUpdateDialog(
        context,
        downloadUrl,
        updateLog,
        mustUpdate,
        buttonColor: buttonColor,
        titleColor: titleColor,
        onConformTap: onConformTap,
        onCancelTap: onCancelTap,
      );
      return;
    }
    final List<PermissionGroup> permissions = <PermissionGroup>[
      PermissionGroup.storage
    ];
    final Map<PermissionGroup, PermissionStatus> permissionRequestResult =
        await PermissionHandler().requestPermissions(permissions);
    PermissionStatus status = permissionRequestResult[PermissionGroup.storage];

    if (status == PermissionStatus.granted) {
      showUpdateDialog(
        context,
        downloadUrl,
        updateLog,
        mustUpdate,
        buttonColor: buttonColor,
        titleColor: titleColor,
        onConformTap: onConformTap,
        onCancelTap: onCancelTap,
      );
    } else {
      DialogUtils.showCommonDialog(context,
          msg: '"获取文件读写权限失败,即将跳转应用信息”>“权限”中开启权限"',
          negativeMsg: '取消',
          positiveMsg: '前往', onDone: () {
        PermissionHandler().openAppSettings().then((openSuccess) {
          if (openSuccess != true) {}
        });
      });
    }
  }

  static showUpdateDialog(
    BuildContext context,
    String downloadUrl,
    String releaseLog,
    bool mustUpdate, {
    Color buttonColor,
    Color titleColor,
    GestureTapCallback onConformTap,
    GestureTapCallback onCancelTap,
  }) {
    showDialog(
        context: context,
        builder: (context) {
          VersionUpdateDialog messageDialog = new VersionUpdateDialog(
            positiveText: "更新",
            versionMsgList: AppUtils.getMsgList(releaseLog),
            mustUpdate: mustUpdate,
            downloadUrl: downloadUrl,
//            minHeight: 160,
            buttonColor: buttonColor,
            titleColor: titleColor,
            onConformTap: onConformTap,
            onCancelTap: onCancelTap,
          );
          return messageDialog;
        });
  }
}

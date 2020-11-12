###集成方式
# aj_flutter_scan

###新增功能
Android支持从相册选择图片识别条形码/二维码

###Fix
Android选择图片过大可能造成的OOM

###集成方式
###一，For Android
```
    <!--请求访问使用照相设备-->
    <uses-permission android:name="android.permission.CAMERA" />
    ## pubspec.yaml引用

    aj_flutter_scan:
       git:
          url: https://github.com/anji-plus/flutter_plugin.git
          path: aj_flutter_scan

```

注意：插件url 如果GitHub下载慢的话 可以换成gitee的url
```
aj_flutter_scan:
    git:
    url: https://gitee.com/anji-plus/aj_flutter_plugins.git
    path: aj_flutter_scan
```
    
 
###二，iOS
```
Deployment Target 设置为 10.0
Podfile 中 target 'Runner' do 添加 use_frameworks! 支持swift
-- iOS需要添加如下内容
  <key>NSCameraUsageDescription</key>
  <string>允许应用在扫码功能中使用您的摄像头</string>
  <key>NSPhotoLibraryUsageDescription</key>
  <string>需要访问你的相册</string>
  <key>NSMicrophoneUsageDescription</key>
  <string>需要访问你的麦克风</string>
```

###三，dart层调用
      （1）引用
```
	  import 'package:aj_flutter_scan/aj_flutter_scan.dart';
``` 
     (2)使用	
```
  //拍照识别二维码/条形码
  Future<void> _getBarcodeFromCamera() async {
    String barCode;
    try {
      barCode =
          await AjFlutterScan.getBarCode(scanTitle: "将二维码/条形码放入框内，即可自动扫描");
    } catch (e) {
      if (e.code == AjFlutterScan.CameraAccessDenied) {
        print("扫描失败,请在iOS\"设置\"-\"隐私\"-\"相机\"中开启权限");
      } else {
        print("Unknown error: $e");
      }
    }
    if (!mounted) return;

    print('barCode is ${barCode}');
    setState(() {
      _barCodeFromCamera = barCode;
    });
  }

  //从相册识别二维码/条形码
  _getBarcodeFromGallery() async {
    String barCode;
    try {
      barCode = await AjFlutterScan.getBarCodeFromGallery();
    } catch (e) {
      if (e.code == AjFlutterScan.CameraAccessDenied) {
        print("扫描失败,请在iOS\"设置\"-\"隐私\"-\"相机\"中开启权限");
      } else {
        print("Unknown error: $e");
      }
    }
    if (!mounted) return;

    setState(() {
      _barCodeFromGallery = barCode;
    });
  }
```

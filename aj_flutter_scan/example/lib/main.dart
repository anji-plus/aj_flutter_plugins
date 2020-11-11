import 'dart:async';

import 'package:aj_flutter_scan/aj_flutter_scan.dart';
import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _barCodeFromCamera = 'Unknown';
  String _barCodeFromGallery = 'Unknown';

  @override
  void initState() {
    super.initState();
  }

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

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('aj_flutter_scan example'),
        ),
        body: Column(
          children: <Widget>[
            Center(
              child: InkWell(
                onTap: () {
                  _getBarcodeFromCamera();
                },
                child: Container(
                  alignment: Alignment.center,
                  constraints: BoxConstraints(minHeight: 60),
                  width: 300,
                  child: Text('点击拍照识别结果 is: $_barCodeFromCamera\n'),
                ),
              ),
            ),
            Center(
              child: InkWell(
                onTap: () {
                  _getBarcodeFromGallery();
                },
                child: Container(
                  alignment: Alignment.center,
                  constraints: BoxConstraints(minHeight: 60),
                  width: 300,
                  child: Text('点击从相册识别结果 is: $_barCodeFromGallery\n'),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}

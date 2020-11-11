import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_umeng_analytics/flutter_umeng_analytics.dart';

import 'aj_crash_collect.dart';

void main() async {
  //记得区分debug和release版本
  if (Platform.isIOS) {
    UMengAnalytics.init("iOS AppKey",
        encrypt: true, reportCrash: false); //app debug
  } else {
    UMengAnalytics.init('5cda29173fc195b666000ced',
        policy: Policy.BATCH, encrypt: true, reportCrash: false);
  }
  AJCrashCollect.init(() {
    runApp(new MyApp());
  });
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
        body: ListView(
            children: [
              new RaisedButton(
                onPressed: () {
                  UMengAnalytics.logEvent("hello");
                },
                child: Container(alignment: Alignment.center,child: new Text('Running on: hello'),)
              ),

              new RaisedButton(
                onPressed: () {
                  List list;
                  print(list.length);
                },
                child: new Text('Running on: reportError 错误统计  添加一个错误信息 \n iOS 需要在友盟后台 添加一个reportError的事件'),
              ),
              Container(
                alignment: Alignment.center,
                height: 200,
                child: Text("测试"),
              ),
              Container(
                alignment: Alignment.center,
                height: 200,
                child: Text("测试"),
              ),
              Container(
                alignment: Alignment.center,
                height: 200,
                child: Text("测试"),
              ),
            ]),
      ),
    );
  }
}

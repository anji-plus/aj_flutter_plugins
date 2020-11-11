import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pda/aj_flutter_pda.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _barCode = 'Unknown';
  BasicMessageChannel _messageChannel; //只接收扫描结果的Channel
  var map;
  String vin;
  bool isPdaDevice;

  @override
  void initState() {
    super.initState();
    _messageChannel =
        new BasicMessageChannel(Commons.pdaMessageChannel, StringCodec());
    receiveMessage();
    isPDA();
    initData();
  }

  //打印标签的数据
  void initData() {
    /*
    line1：车架号
    line2： 堆场 库区 道位
    line3: 堆场 库区 道位(描述)
    line4: 船名 船次 日期
    line5: 条形码
    * */
    List line1 = [];
    List line2 = [];
    List line3 = [];
    List line4 = [];
    List line5 = [];
    var line1map1 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 2,
      "barWidth": 100,
      "barHeight": 100,
      "bold": true,
      "underLine": true,
      "fontSize": 30,
      "title": "LSVFA49J132037043",
      "byteMapPath": "",
      "mode": 1
    };
    line1.add(line1map1);

    var line2map1 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 1,
      "barWidth": 100,
      "barHeight": 100,
      "bold": false,
      "underLine": false,
      "fontSize": 24,
      "title": "堆场",
      "byteMapPath": "",
      "mode": 1
    };
    var line2map2 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 2,
      "barWidth": 100,
      "barHeight": 100,
      "bold": false,
      "underLine": false,
      "fontSize": 24,
      "title": "库区",
      "byteMapPath": "",
      "mode": 1
    };
    var line2map3 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 3,
      "barWidth": 100,
      "barHeight": 100,
      "bold": false,
      "underLine": false,
      "fontSize": 24,
      "title": "道位",
      "byteMapPath": "",
      "mode": 1
    };
    line2.add(line2map1);
    line2.add(line2map2);
    line2.add(line2map3);

    var line3map1 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 1,
      "barWidth": 100,
      "barHeight": 100,
      "bold": true,
      "underLine": false,
      "fontSize": 30,
      "title": "5，6号库",
      "byteMapPath": "",
      "mode": 1
    };
    var line3map2 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 2,
      "barWidth": 100,
      "barHeight": 100,
      "bold": true,
      "underLine": false,
      "fontSize": 30,
      "title": "A",
      "byteMapPath": "",
      "mode": 1
    };
    var line3map3 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 3,
      "barWidth": 100,
      "barHeight": 100,
      "bold": true,
      "underLine": false,
      "fontSize": 30,
      "title": "025",
      "byteMapPath": "",
      "mode": 1
    };
    line3.add(line3map1);
    line3.add(line3map2);
    line3.add(line3map3);
    var line4map1 = {
      "marginLeft": 10,
      "marginRight": 10,
      "gravity": 1,
      "barWidth": 100,
      "barHeight": 100,
      "bold": false,
      "underLine": true,
      "fontSize": 20,
      "title": "安吉208|V0001 2018 08 27 16 05 17",
      "byteMapPath": "",
      "mode": 1
    };
    line4.add(line4map1);
    var line5map1 = {
      "marginLeft": 20,
      "marginRight": 10,
      "gravity": 1,
      "barWidth": 45 * 8,
      "barHeight": 8 * 8,
      "bold": false,
      "underLine": true,
      "fontSize": 20,
      "title": "LSVFA49J132037043",
      "byteMapPath": "",
      "mode": 2
    };
    line5.add(line5map1);

    List item = [];
    item.add(line1);
    item.add(line2);
    item.add(line3);
    item.add(line4);
    item.add(line5);
    map = {"printer": item};
  }

  // 判断是否是pda
  Future<bool> isPDA() async {
    try {
      isPdaDevice = await Pda.isPda();
    } on Exception {
      isPdaDevice = false;
    }
    if(isPdaDevice){
      _barCode = "是pda";
    }else{
      _barCode = "不是pda";
    }
    setState(() {
    });
  }

  // 扫描
  //TODO 扫描
  Future<void> scan() async {
    String barCode;
    try {
      barCode = await Pda.startScan();
    } on PlatformException {
      barCode = 'Failed to get platform version.';
    }
    if (!mounted) return;
    setState(() {
      _barCode = barCode;
    });
  }

  //接收侧边按钮扫描消息监听
  //TODO 侧边监听扫描
  void receiveMessage() {
    _messageChannel.setMessageHandler((result) async {
      setState(() {
        _barCode = result;
      });
    });
  }

  //停止扫描
  stopScan() {
    Pda.stopScan();
  }

  //打印
  printTags() {
    Pda.print(map);
  }

  // 扫描车架号
  // TODO RFID标签扫描
  Future<void> readRFIDCode() async {
    String readRFIDCode;
    try {
      readRFIDCode = await Pda.readRFIDCode(isNeedDialog: false);
    } on Exception {
      readRFIDCode = '读取车架号失败';
    }
    if (!mounted) return;
    print('readRFIDCode ${readRFIDCode}');
    setState(() {
      _barCode = readRFIDCode;
    });
  }

  // 制卡
  // TODO RFID标签写
  Future<void> WriteRFIDCode(String vin) async {
    String result;
    try {
     int iswrite = await Pda.WriteRFIDCode(vin,isNeedDialog: true);
     if(iswrite==0){
       result = '制卡成功';
     }else{
       result = '制卡失败1';
     }
      print('result ${result}');
    } on Exception {
      result = '制卡失败2';
    }
    if (!mounted) return;
    setState(() {
      _barCode = result;
    });
  }

  @override
  void dispose() {
    super.dispose();
    stopScan();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              InkWell(
                child: Container(
                  alignment: Alignment.center,
                  width: 200,
                  height: 50,
                  child: Text("扫描"),
                ),
                onTap: () {
                  scan();
                },
              ),
              Text('Running on: $_barCode\n'),
              InkWell(
                child: Container(
                  alignment: Alignment.center,
                  width: 200,
                  height: 50,
                  child: Text("打印"),
                ),
                onTap: () {
                  //打印两张标签
                  for (int i = 0; i < 2; i++) {
                    printTags();
                  }
                },
              ),
              InkWell(
                child: Container(
                  alignment: Alignment.center,
                  width: 200,
                  height: 50,
                  child: Text("读取车架号"),
                ),
                onTap: () {
                  readRFIDCode();
                },
              ),
              TextField(
                  onChanged: (text) {
                    vin = text;
                  },
                  textAlign: TextAlign.left,
                  style: TextStyle(fontSize: 14),
                  decoration: InputDecoration(
                    hintText: '请扫VIN码',
                    contentPadding: EdgeInsets.only(bottom: 0, left: 20),
                    hintStyle: TextStyle(
                      fontSize: 14,
                    ),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Colors.grey, width: 0.5),
                      borderRadius: BorderRadius.circular(30),
                    ),
                    enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(30),
                        borderSide: BorderSide(color: Colors.grey, width: 0.5)),
                  )),
              InkWell(
                child: Container(
                  alignment: Alignment.center,
                  width: 200,
                  height: 50,
                  child: Text("制卡"),
                ),
                onTap: () {
                  WriteRFIDCode(vin);
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}

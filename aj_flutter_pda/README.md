###集成方式
# aj_flutter_pda


###集成方式
一、pubspec.yaml引用

    pda:
    git:
    url: https://github.com/anji-plus/flutter_plugin.git
    path: aj_flutter_pda

    Note：在Android中引用armeabi-v7a包，
    debug模式下若找不到so文件，且flutter sdk位1.5.4以下包含1.5.4，则置Additional arguments为
    --target-platform=android-arm，或者使用flutter run --target-platform=android-arm命令安装

```

###二、dart层调用
  1、引用：import 'package:pda/pda.dart';
  2、使用：
1）点击按钮扫描二维码或条形码:Pda.startScan();
2）侧边按钮点击扫描二维码或条形码：
	BasicMessageChannel_messageChannel =
	new BasicMessageChannel('com.anjiplus.pdasend', StringCodec())
	//接收侧边按钮扫描消息监听
	void receiveMessage() {
	  _messageChannel.setMessageHandler((result) async {
		setState(() {
		  _barCode = result;
		});
	  });
	}
3）停止扫描：Pda.stopScan();
4）打印标签：Pda.print(Map map);
5）走纸一张：Pda.goNextPage();
6）识别车架号：Pda.readRFIDCode(bool isNeedDialog);
7）制卡：Pda.readRFIDCode(String vin，bool isNeedDialog);
注：1）、2）、6）有返回值为String类型，7返回int类型，其他无返回值；
```

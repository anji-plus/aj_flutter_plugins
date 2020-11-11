# aj_flutter_plugin

A new Flutter plugin.

## Getting Started
flutter与原生交互，包含静态资源获取，版本获取。


###集成方式
```
aj_flutter_plugin:
git:
url: https://github.com/anji-plus/flutter_plugin.git
path: aj_flutter_plugin

```

###使用方式

```

class _MyAppState extends State<MyApp> {
    String _platformVersion = 'Unknown';
    int firstBackPressTime = 0;

    @override
    void initState() {
        super.initState();
        initPlatformState();
    }

    // Platform messages are asynchronous, so we initialize in an async method.
    Future<void> initPlatformState() async {
        String platformVersion;
        AjFlutterPlugin info;
        // Platform messages may fail, so we use a try/catch PlatformException.
        try {
            info = await AjFlutterPlugin.platformVersion();
            platformVersion = "appName:${info.appName}\n" +
                "packageName: ${info.packageName}\n" +
                "version:${info.version}\n" +
                "buildNumber:${info.buildNumber}";

                print(platformVersion);
            } on PlatformException {
        platformVersion = 'Failed to get platform version.';
        }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
        if (!mounted) return;

    setState(() {
        _platformVersion = platformVersion;
        });
    }

    @override
    Widget build(BuildContext context) {
        return MaterialApp(
            home: WillPopScope(
            child: Scaffold(
            appBar: AppBar(
            title: const Text('Plugin example app'),
            ),
            body: Center(
            child: ListView(
            children: <Widget>[
            Text('$_platformVersion\n'),
                FlatButton(
                color: Colors.black12,
                onPressed: () async {
            if (Platform.isAndroid) {
                if (await canLaunch("https://www.pgyer.com/Ti9R")) {
                    await launch("https://www.pgyer.com/Ti9R");
                    }
                } else if (Platform.isIOS) {
                    await launch("http://itunes.apple.com/cn/app/id1441492772");
                    }
                },
            child: Text("跳转外部链接"),
            ),

            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    await launchCallPhone('10086');
                    },
                child: Text("打电话 100000"),
            ),

            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    await launchEmail('100000');
                },
                child: Text("发邮件 100000"),
            ),

            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    await launchMessage('188188888888');
                    },
                child: Text("发信息 10086"),
            ),

            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    _exitApp();
                },
                child: Text("退出app"),
            ),

            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    print(await isiOSSimuLator());
                },
                child: Text("是否是模拟器"),
            ),
            FlatButton(
                color: Colors.black12,
                onPressed: () async {
                    int permissState = await AjFlutterPlugin.getLocationPermissions();
                    _platformVersion = "$permissState";
                    setState(() {
                    });
                },
                child: Text("获取权限"),
            ),
            //getLocationPermissions

            ],
        )),
    ),
    onWillPop: () {
        _exitApp();
    },
    ),);
    
}

    _exitApp() {
        //Android 回到桌面
        int secondTime = new DateTime.now().millisecondsSinceEpoch;
        if (secondTime - firstBackPressTime > 2000) {
            // 如果两次按键时间间隔大于2秒，则不退出
            firstBackPressTime = secondTime; // 更新firstTime
            print("再按一次退出程序");
        } else {
            //退出到桌面
            exitApp();
            }
        }
}

```

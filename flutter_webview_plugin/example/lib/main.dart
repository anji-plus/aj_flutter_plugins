import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:flutter_webview_plugin/flutter_webview_plugin.dart';

const kAndroidUserAgent =
    'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36';

//String selectedUrl = 'http://10.108.11.46/mweb/#/appMonitorChartList';
String selectedUrl =
    'https://desk.anji-plus.com/#/feedback/app?code=D000000140&userName=qizhiyuan&sgin=67A8EE398FA7DEFE31003E2A28B24DFA&iphone=18810092406&email=qizhiyuan@anji-plus.com';

// http://10.108.11.46/mweb/#/appMonitorChartList??token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiZXhwIjoxNTY0OTEyNTM0fQ.eb8KstYlIOT7NwM4TPKO35HlpLm7Hl_P5p8KdBXBkDhgxX6qV0puvb9FQxeJ6jVCJuef-sCEzCwQ2Sb95VpR7Q&projectId=8&connectName=0&plateform=3
//flutter: str.length 255
void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final flutterWebViewPlugin = FlutterWebviewPlugin();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter WebView Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter WebView Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // Instance of WebView plugin
  final flutterWebViewPlugin = FlutterWebviewPlugin();

  // On destroy stream
  StreamSubscription _onDestroy;

  // On urlChanged stream
  StreamSubscription<String> _onUrlChanged;

  // On urlChanged stream
  StreamSubscription<WebViewStateChanged> _onStateChanged;

  StreamSubscription<WebViewHttpError> _onHttpError;

  StreamSubscription<double> _onProgressChanged;

  StreamSubscription<double> _onScrollYChanged;

  StreamSubscription<double> _onScrollXChanged;
  StreamSubscription<JavascriptChannel> _onJavascriptChannelMessage;

  final _urlCtrl = TextEditingController(text: selectedUrl);

  final _codeCtrl = TextEditingController(text: 'window.navigator.userAgent');

  final _scaffoldKey = GlobalKey<ScaffoldState>();

  final _history = [];

  @override
  void initState() {
    super.initState();
    print("ssss");
    flutterWebViewPlugin.close();

    _urlCtrl.addListener(() {
      selectedUrl = _urlCtrl.text;
    });

    // Add a listener to on destroy WebView, so you can make came actions.
    _onDestroy = flutterWebViewPlugin.onDestroy.listen((_) {
      if (mounted) {
        // Actions like show a info toast.
        _scaffoldKey.currentState.showSnackBar(
            const SnackBar(content: const Text('Webview Destroyed')));
      }
    });

    // Add a listener to on url changed
    _onUrlChanged = flutterWebViewPlugin.onUrlChanged.listen((String url) {
      if (mounted) {
        setState(() {
          _history.add('onUrlChanged: $url');
        });
      }
    });

    _onProgressChanged =
        flutterWebViewPlugin.onProgressChanged.listen((double progress) {
      if (mounted) {
        setState(() {
          _history.add("onProgressChanged: $progress");
        });
      }
    });

    _onScrollYChanged =
        flutterWebViewPlugin.onScrollYChanged.listen((double y) {
      if (mounted) {
        setState(() {
          _history.add('Scroll in Y Direction: $y');
        });
      }
    });

    _onScrollXChanged =
        flutterWebViewPlugin.onScrollXChanged.listen((double x) {
      if (mounted) {
        setState(() {
          _history.add('Scroll in X Direction: $x');
        });
      }
    });

    _onStateChanged =
        flutterWebViewPlugin.onStateChanged.listen((WebViewStateChanged state) {
      if (mounted) {
        setState(() {
          _history.add('onStateChanged: ${state.type} ${state.url}');
        });
      }
    });

    _onHttpError =
        flutterWebViewPlugin.onHttpError.listen((WebViewHttpError error) {
      if (mounted) {
        setState(() {
          _history.add('onHttpError: ${error.code} ${error.url}');
        });
      }
    });

    _onJavascriptChannelMessage = flutterWebViewPlugin
        .onJavascriptChannelMessage
        .listen((JavascriptChannel javascriptChannel) {
      print(
          "${javascriptChannel.message}   --- ${javascriptChannel.channelName}");

      if (javascriptChannel.channelName == "NativeJavascriptChannel") {
        if (javascriptChannel.message == "setProjectInfo") {
          //TODO 索取筛选信息
          var map = {
            "token":
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiZXhwIjoxNTY0OTEyNTM0fQ.eb8KstYlIOT7NwM4TPKO35HlpLm7Hl_P5p8KdBXBkDhgxX6qV0puvb9FQxeJ6jVCJuef-sCEzCwQ2Sb95VpR7Q",
            "projectId": 8,
            "connectName": 0, //网络状态
            "plateform": 3, //APP来源  Android： 1，  iOS真机： 2 ,   iOS 模拟器： 3,
          };
          String mapStr = json.encode(map);
          flutterWebViewPlugin.evalJavascript("getProjectInfo($mapStr)");
        }
      }
    });
  }

  @override
  void dispose() {
    // Every listener should be canceled, the same should be done with this stream.
    _onDestroy.cancel();
    _onUrlChanged.cancel();
    _onStateChanged.cancel();
    _onHttpError.cancel();
    _onProgressChanged.cancel();
    _onScrollXChanged.cancel();
    _onScrollYChanged.cancel();
    _onJavascriptChannelMessage.cancel();
    flutterWebViewPlugin.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return WebviewScaffold(
      url: selectedUrl,
      appBar: AppBar(
        title: const Text('Widget WebView'),
      ),
      withZoom: true,
      javascriptChannels: <String>["NativeJavascriptChannel"],
      withLocalStorage: true,
      hidden: true,
      initialChild: Container(
        color: Colors.redAccent,
        child: const Center(
          child: Text('Waiting.....'),
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        child: Row(
          children: <Widget>[
            IconButton(
              icon: const Icon(Icons.arrow_back_ios),
              onPressed: () {
                flutterWebViewPlugin.goBack();
              },
            ),
            IconButton(
              icon: const Icon(Icons.arrow_forward_ios),
              onPressed: () {
                flutterWebViewPlugin.goForward();
              },
            ),
            IconButton(
              icon: const Icon(Icons.autorenew),
              onPressed: () {
                flutterWebViewPlugin.reload();
              },
            ),
          ],
        ),
      ),
    );
  }
}

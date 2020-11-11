
# Flutter WebView Plugin

## 原生webview
###集成方式
```
flutter_webview_plugin:
git:
url: http://gitlab.anji-allways.com/mobileteam/modules.git
path: flutter_webview_plugin

```


#### iOS

要使插件正确工作，您需要添加新的关键字到 `ios/Runner/Info.plist`

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
    <key>NSAllowsArbitraryLoadsInWebContent</key>
    <true/>
</dict>
```
iOS9以上需要添加`NSAllowsArbitraryLoads` 
iOS10+还需要添加`NSAllowsArbitraryLoadsInWebContent`


#### 添加一个webview 如下
```dart
new MaterialApp(
      routes: {
        "/": (_) => new WebviewScaffold(
          url: "https://www.google.com",
          appBar: new AppBar(
            title: new Text("Widget webview"),
          ),
        ),
      },
    );
```

可选参数`hidden`和`initialChild`可用，以便在等待页面加载时显示其他内容。
如果将`hidden`设置为true，它将显示一个默认的`CircularProgressIndicator`。如果另外为`initialChild`指定小部件
你可以让它显示任何你喜欢的直到页面加载。

下面将显示一个显示文本`waiting…`的reading屏幕。
```dart
return new MaterialApp(
  title: 'Flutter WebView Demo',
  theme: new ThemeData(
    primarySwatch: Colors.blue,
  ),
  routes: {
    '/': (_) => const MyHomePage(title: 'Flutter WebView Demo'),
    '/widget': (_) => new WebviewScaffold(
      url: selectedUrl,
      appBar: new AppBar(
        title: const Text('Widget webview'),
      ),
      withZoom: true,
      withLocalStorage: true,
      javascriptChannels: <String>[
      "NativeJavascriptChannel"
      ],
      hidden: true,
      initialChild: Container(
        color: Colors.redAccent,
        child: const Center(
          child: Text('Waiting.....'),
        ),
      ),
    ),
  },
);
```

`FlutterWebviewPlugin`提供了一个单独的实例，链接到一个独特的webview，
所以你可以从应用程序的任何地方控制webview

listen for events

```dart
final flutterWebviewPlugin = new FlutterWebviewPlugin();

flutterWebviewPlugin.onUrlChanged.listen((String url) {

});
```

#### 监听webview中的滚动事件

```dart
final flutterWebviewPlugin = new FlutterWebviewPlugin();
flutterWebviewPlugin.onScrollYChanged.listen((double offsetY) { // latest offset value in vertical scroll
  // compare vertical scroll changes here with old value
});

flutterWebviewPlugin.onScrollXChanged.listen((double offsetX) { // latest offset value in horizontal scroll
  // compare horizontal scroll changes here with old value
});

````

注意:ios和android之间的滚动距离略有不同。Android的滚动值差异往往大于ios设备。


#### 隐藏的WebView

```dart
final flutterWebviewPlugin = new FlutterWebviewPlugin();

flutterWebviewPlugin.launch(url, hidden: true);
```

#### 关闭 WebView

```dart
flutterWebviewPlugin.close();
```

####自定义矩形内的Webview

```dart
final flutterWebviewPlugin = new FlutterWebviewPlugin();

flutterWebviewPlugin.launch(url,
  fullScreen: false,
  rect: new Rect.fromLTWH(
    0.0,
    0.0,
    MediaQuery.of(context).size.width,
    300.0,
  ),
);
```

#### 将定制代码注入webview
使用`flutterWebviewPlugin。evalJavaScript(String code)`。这个函数必须在页面加载完成后运行(例如，在状态为`finishLoad`的事件中监听`onStateChanged`)。

如果要嵌入大量JavaScript，请使用资产文件。将资产文件添加到`pubspec.yaml`,。然后像这样调用函数：

```dart
Future<String> loadJS(String name) async {
  var givenJS = rootBundle.loadString('assets/$name.js');
  return givenJS.then((String js) {
    flutterWebViewPlugin.onStateChanged.listen((viewState) async {
      if (viewState.type == WebViewState.finishLoad) {
        flutterWebViewPlugin.evalJavascript(js);
      }
    });
  });
}
```


### Webview Events

- `Stream<Null>` onDestroy
- `Stream<String>` onUrlChanged
- `Stream<WebViewStateChanged>` onStateChanged
- `Stream<double>` onScrollXChanged
- `Stream<double>` onScrollYChanged
- `Stream<String>` onError
- `StreamSubscription<JavascriptChannel>`  _onJavascriptChannelMessage

**_不要忘记处理释放webview _**
`flutterWebviewPlugin.dispose()`

### Webview Functions

```dart
Future<Null> launch(String url, {
   Map<String, String> headers: null,
   bool withJavascript: true,
   bool clearCache: false,
   bool clearCookies: false,
   bool hidden: false,
   bool enableAppScheme: true,
   Rect rect: null,
   String userAgent: null,
   bool withZoom: false,
   bool withLocalStorage: true,
   bool withLocalUrl: true,
   bool scrollBar: true,
   bool supportMultipleWindows: false,
   bool appCacheEnabled: false,
   bool allowFileURLs: false,
   List<String> javascriptChannels
});
```

```dart
Future<String> evalJavascript(String code);
```

```dart
Future<Map<String, dynamic>> getCookies();
```

```dart
Future<Null> cleanCookies();
```

```dart
Future<Null> resize(Rect rect);
```

```dart
Future<Null> show();
```

```dart
Future<Null> hide();
```

```dart
Future<Null> reloadUrl(String url);
```

```dart
Future<Null> close();
```

```dart
Future<Null> reload();
```

```dart
Future<Null> goBack();
```

```dart
Future<Null> goForward();
```

```dart
Future<Null> stopLoading();
```


#### 使用javascriptChannels
初始化注册`javascriptChannels`
```
        javascriptChannels: <String>[
            "NativeJavascriptChannel"
        ],
```
注册监听
- `StreamSubscription<JavascriptChannel>`  _onJavascriptChannelMessage

监听回调
```
    _onJavascriptChannelMessage = flutterWebViewPlugin
        .onJavascriptChannelMessage
        .listen((JavascriptChannel javascriptChannel) {
            print("${javascriptChannel.message}   --- ${javascriptChannel.channelName}");

            if(javascriptChannel.channelName == "NativeJavascriptChannel"){
                if (javascriptChannel.message == "setProjectInfo") { //TODO 索取筛选信息
                    var map = {
                        "token" : "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5IiwiZXhwIjoxNTY0OTEyNTM0fQ.eb8KstYlIOT7NwM4TPKO35HlpLm7Hl_P5p8KdBXBkDhgxX6qV0puvb9FQxeJ6jVCJuef-sCEzCwQ2Sb95VpR7Q",
                        "projectId": 8,
                        "connectName": 0,//网络状态
                        "plateform": 3, //APP来源  Android： 1，  iOS真机： 2 ,   iOS 模拟器： 3,
                    };
                String mapStr = json.encode(map);
                flutterWebViewPlugin.evalJavascript("getProjectInfo($mapStr)");
               }
            }
        });
```



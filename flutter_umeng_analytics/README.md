# flutter_umeng_analytics
## 该插件是在[umeng:analytics](http://mobile.umeng.com/analytics)基础上做的处理，完善友盟统计接口
Flutter plugin for [umeng:analytics](http://mobile.umeng.com/analytics)
##Install
pubspec.yaml add:

```
flutter_umeng_analytics:
    git:
      url: https://github.com/anji-plus/flutter_plugin.git
      path: flutter_umeng_analytics
```
注意：插件url 如果GitHub下载慢的话 可以换成gitee的url
```
flutter_umeng_analytics:
    git:
    url: https://gitee.com/anji-plus/aj_flutter_plugins.git
    path: flutter_umeng_analytics
```

## Usage

### Init
```dart
import 'package:platform/platform.dart';
//debug or release version?
final Platform platform = const LocalPlatform();
if (platform.isAndroid)
  UMengAnalytics.init('Android AppKey',
          policy: Policy.BATCH, encrypt: true, reportCrash: false);
else if (platform.isIOS)
  UMengAnalytics.init('iOS AppKey',
          policy: Policy.BATCH, encrypt: true, reportCrash: false);
```

### Log page
```dart
initState() {
  super.initState();

  UMengAnalytics.beginPageView("home");
}

dispose() {
  super.dispose();

  UMengAnalytics.endPageView("home");
}

someFunction() {
  UMengAnalytics.logEvent("some click");
}
```

## Getting Started

For help getting started with Flutter, view our online
[documentation](http://flutter.io/).

For help on editing plugin code, view the [documentation](https://flutter.io/platform-plugins/#edit-code).

# aj_flutter_update

###集成方式

###一，For Android

    ##1，pubspec.yaml引用

    aj_flutter_update:
       git:
          url: https://github.com/anji-plus/flutter_plugin.git
          path: aj_flutter_update
    注意：插件url 如果GitHub下载慢的话 可以换成gitee的url
    aj_flutter_update:
      git:
      url: https://gitee.com/anji-plus/aj_flutter_plugins.git
      path: aj_flutter_update
              

    ##2，权限添加，在AndroidManifest.xml加入
      <uses-permission android:name="android.permission.INTERNET" />
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	  <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
    ##3，权限添加，在gradle.properties加入AndroidX
    android.useAndroidX = true
    android.enableJetifier = true

    ##4，兼容android7.0+，继续在AndroidManifest.xml加入
     <provider
           android:name="androidx.core.content.FileProvider"
           android:authorities="anjiplus.aj_flutter_update_example.fileprovider"
           android:exported="false"
           android:grantUriPermissions="true">
              <meta-data
                 android:name="android.support.FILE_PROVIDER_PATHS"
                 android:resource="@xml/file_paths" />
      </provider>

     Note："anjiplus.aj_flutter_update_example"是example的包名，所以
        authorities为 "包名.fileprovider" ，注意file_paths.xml里的path为
        <external-path path="Android/data/anjiplus.aj_flutter_update_example/" name="files_root" />，
        这里的"anjiplus.aj_flutter_update_example"就是包名

###二，iOS
```
Deployment Target 设置为 10.0
Podfile 中 target 'Runner' do 添加 use_frameworks! 支持swift
```

###三，dart层调用
      （1）引用
      import 'package:aj_flutter_update/aj_flutter_update.dart';
      class _AppPageState extends State<AppWidget> with AjFlutterUpdateMixin
      这里要加入mixin AjFlutterUpdateMixin

     (2)使用
     AjFlutterUpdateMixin.versionUpdate(
     context: context,
     downloadUrl: "https://s3.cn-north-1.amazonaws.com.cn/anjiplus-ftp/6c2e042d-075f-450a-86eb-459f3722a7ad5084544252265841842.apk",//iOS App StoreURL 、Android apk下载url
     updateLog: "1，我的老哥==2，你的老妹",//updateLog 以 == 做分割
     mustUpdate: false, //是否强制更新
     titleColor: Color(0xFFFFA033), //标题颜色
     buttonColor: Colors.blue, //按钮颜色
     );


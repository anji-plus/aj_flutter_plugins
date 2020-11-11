# Anji-plus Flutter Plugins

A new Flutter plugin.

## Getting Started
flutter与原生交互，包含静态资源获取，版本获取。

**FlutterFire Plugins** 
```
- 使用方式
xxx:
git:
url: http://gitlab.anji-allways.com/mobileteam/modules.git
path: xxx
```

1、 [aj_flutter_plugin](./aj_flutter_plugin/) 
使用详细描述[【README.md】](./aj_flutter_plugin/README.md))

2、[aj_flutter_update](./aj_flutter_update/) 
使用详细描述[【README.md】](./aj_flutter_update/README.md))

  ```
  dio 2.0以上版本
  - iOS需要添加如下内容
      Deployment Target 设置为 10.0
      Podfile 中 target 'Runner' do 添加 use_frameworks! 支持swift
  
  - Android需要添加如下内容
      <!-- permission plugin 需要的权限 -->
      <!-- 写入权限 -->
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
      <!-- 读取权限 -->
      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
      <!-- 网络权限 -->
      <uses-permission android:name="android.permission.INTERNET"/>
      
      **兼容android7.0+，继续在AndroidManifest.xml加入
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
  
 ```
3、 [aj_flutter_auto_orientation](./aj_flutter_auto_orientation/) 
使用详细描述[【README.md】](./aj_flutter_auto_orientation/README.md))


4、[aj_flutter_scan](./aj_flutter_scan/) 
使用详细描述[【README.md】](./aj_flutter_scan/README.md))
```
    **aj_flutter_scan**
    - iOS需要添加如下内容
    <key>NSCameraUsageDescription</key>
    <string>允许应用在扫码功能中使用您的摄像头</string>
    <key>NSPhotoLibraryUsageDescription</key>
    <string>需要访问你的相册</string>
    <key>NSMicrophoneUsageDescription</key>
    <string>需要访问你的麦克风</string>
```


 5、[flutter_webview_plugin](./flutter_webview_plugin/) 
 使用详细描述[【README.md】](./flutter_webview_plugin/README.md))


6、[image_picker](./image_picker/) 
使用详细描述[【README.md】](./image_picker/README.md))


7、[aj_flutter_component](./aj_flutter_component/) 
使用详细描述[【README.md】](./aj_flutter_component/README.md))


8、[flutter_x5webview_plugin](./flutter_x5webview_plugin/) 
 使用详细描述[【README.md】](./flutter_x5webview_plugin/README.md))
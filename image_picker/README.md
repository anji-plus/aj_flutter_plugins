# Image Picker plugin for Flutter

## 该插件是[imagePicker](https://pub.flutter-io.cn/packages/image_picker)经过修改解决Android图片旋转异常及图片压缩处理异常问题

### iOS

需要在`Info.plist`添加如下权限

* `NSPhotoLibraryUsageDescription`
* `NSCameraUsageDescription` 
* `NSMicrophoneUsageDescription` 

### Android

不需要配置-插件应该开箱即用。
### Example

``` dart
import 'package:image_picker/image_picker.dart';

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  File _image;

  Future getImage() async {
    var image = await ImagePicker.pickImage(source: ImageSource.camera);

    setState(() {
      _image = image;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Image Picker Example'),
      ),
      body: Center(
        child: _image == null
            ? Text('No image selected.')
            : Image.file(_image),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: getImage,
        tooltip: 'Pick Image',
        child: Icon(Icons.add_a_photo),
      ),
    );
  }
}
```

### 处理Android上的主活动销毁

Android系统——尽管很少——有时会在image_picker完成后杀死主活动。当这种情况发生时，我们丢失了从image_picker中选择的数据。在这种情况下，可以使用`retrieveLostData`来检索丢失的数据。例如:

```dart
Future<void> retrieveLostData() async {
  final LostDataResponse response =
      await ImagePicker.retrieveLostData();
  if (response == null) {
    return;
  }
  if (response.file != null) {
    setState(() {
      if (response.type == RetrieveType.video) {
        _handleVideo(response.file);
      } else {
        _handleImage(response.file);
      }
    });
  } else {
    _handleError(response.exception);
  }
}
```

无法检测何时发生这种情况，因此在正确的位置调用此方法是必要的。我们建议将其连接到某种启动检查中。请参考示例应用程序了解我们如何使用它。

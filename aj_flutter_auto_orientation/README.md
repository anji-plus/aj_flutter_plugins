# aj_flutter_auto_orientation

A new Flutter plugin.

## 强制切换横竖屏（锁定方向也可以切换）
###集成方式
```
aj_flutter_auto_orientation:
git:
url: https://github.com/anji-plus/flutter_plugin.git
path: aj_flutter_auto_orientation

```
注意：插件url 如果GitHub下载慢的话 可以换成gitee的url
```
aj_flutter_auto_orientation:
git:
url: https://gitee.com/anji-plus/aj_flutter_plugins.git
path: aj_flutter_auto_orientation
```

## Getting Started
```
//屏幕方向向左
await AjFlutterAutoOrientation.landscapeLeftMode()
//屏幕方向向右
await AjFlutterAutoOrientation.landscapeRightMode()
//屏幕方向向上
await AjFlutterAutoOrientation.landscapeRightMode()
//屏幕方向向下
await AjFlutterAutoOrientation.portraitDownMode()
```

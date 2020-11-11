import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';

final options = [
  AJActionsheetItem(
      label: '选项一',
      value: '1'
  ),
  AJActionsheetItem(
      label: '选项二',
      value: '2'
  ),
  AJActionsheetItem(
      label: '选项三',
      value: '3'
  )
];


class ActionsheetPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final AJActionsheetAndroid actionsheetAndroid = AJActionsheet.android(context);
    final AJActionsheetIos actionsheetIos = AJActionsheet.ios(context);

    return Sample(
        'Actionsheet',
        describe: '弹出式菜单',
        child: Column(
            children: <Widget>[
              AJButton('android', theme: AJButtonType.primary, onClick: () {
                actionsheetAndroid(
                    options: options,
                    onChange: (String value) {
                      AJToast.info(context)('选择了$value');
                    },
                    onClose: () {
                      AJToast.info(context)('关闭');
                    }
                );
              }),
              Container(height: 20.0),
              AJButton('ios', theme: AJButtonType.primary, onClick: () {
                actionsheetIos(
                    title: '请选择',
                    options: options,
                    cancelButton: '取消',
                    onChange: (String value) {
                      AJToast.info(context)('选择了$value');
                    },
                    onClose: () {
                      AJToast.info(context)('关闭');
                    }
                );
              }),
              Container(height: 20.0),
              AJButton('ios 无取消按钮', theme: AJButtonType.primary, onClick: () {
                actionsheetIos(
                    title: '请选择',
                    options: options,
                    onChange: (String value) {
                      AJToast.info(context)('选择了$value');
                    },
                    onClose: () {
                      AJToast.info(context)('关闭');
                    }
                );
              }),
              Container(height: 20.0),
              AJButton('android 禁止遮罩层点击', theme: AJButtonType.primary, onClick: () {
                actionsheetAndroid(
                    options: options,
                    maskClosable: false,
                    onChange: (String value) {
                      AJToast.info(context)('选择了$value');
                    },
                    onClose: () {
                      AJToast.info(context)('关闭');
                    }
                );
              }),
              Container(height: 20.0),
              AJButton('ios 禁止遮罩层点击', theme: AJButtonType.primary, onClick: () {
                actionsheetIos(
                    title: '请选择',
                    options: options,
                    maskClosable: false,
                    cancelButton: '取消',
                    onChange: (String value) {
                      AJToast.info(context)('选择了$value');
                    },
                    onClose: () {
                      AJToast.info(context)('关闭');
                    }
                );
              }),
            ]
        )
    );
  }
}

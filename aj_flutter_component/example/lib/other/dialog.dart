import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';
class DialogPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Dialog',
        describe: '对话框',
        child: Column(
            children: <Widget>[
              TextTitle('ios 主题', noPadding: true),
              AJButton('alert', theme: AJButtonType.primary, onClick: () {
                AJDialog.alert(context)(
                    '弹窗内容',
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('alert - 无标题', theme: AJButtonType.primary, onClick: () {
                AJDialog.alert(context)(
                    '弹窗内容',
                    title: null,
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('confirm', theme: AJButtonType.primary, onClick: () {
                AJDialog.confirm(context)(
                    '弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内',
                    onCancel: () {
                      AJToast.info(context)('点击了取消');
                    },
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('confirm - 无标题', theme: AJButtonType.primary, onClick: () {
                AJDialog.confirm(context)(
                    '弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内',
                    title: null,
                    onCancel: () {
                      AJToast.info(context)('点击了取消');
                    },
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              TextTitle('android 主题', noPadding: true),
              AJButton('alert', theme: AJButtonType.primary, onClick: () {
                AJDialog.alert(context)(
                    '弹窗内容',
                    theme: AJDialogTheme.android,
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('alert - 无标题', theme: AJButtonType.primary, onClick: () {
                AJDialog.alert(context)(
                    '弹窗内容',
                    title: null,
                    theme: AJDialogTheme.android,
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('confirm', theme: AJButtonType.primary, onClick: () {
                AJDialog.confirm(context)(
                    '弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内',
                    theme: AJDialogTheme.android,
                    onCancel: () {
                      AJToast.info(context)('点击了取消');
                    },
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              }),
              SizedBox(height: 20.0),
              AJButton('confirm - 无标题', theme: AJButtonType.primary, onClick: () {
                AJDialog.confirm(context)(
                    '弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内',
                    theme: AJDialogTheme.android,
                    title: null,
                    onCancel: () {
                      AJToast.info(context)('点击了取消');
                    },
                    onConfirm: () {
                      AJToast.info(context)('点击了确认');
                    }
                );
              })
            ]
        )
    );
  }
}
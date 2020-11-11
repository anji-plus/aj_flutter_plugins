import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

Widget box(Widget child) {
  return Padding(
      padding: EdgeInsets.only(left: viewPadding, right: viewPadding, top: viewPadding),
      child: child
  );
}

class InputPage extends StatefulWidget {
  @override
  _InputPageState createState() => _InputPageState();
}

class _InputPageState extends State {
  String value = '';
  GlobalKey inputKey = GlobalKey();
  GlobalKey formKey = GlobalKey();

  @override
  Widget build(BuildContext context) {
    return Sample(
        'Input',
        describe: '表单输入',
        showPadding: false,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              TextTitle('表单'),
              AJForm(
                  key: formKey,
                  children: <Widget>[
                    AJInput(
                        label: '标题标题',
                        hintText: '请输入姓名'
                    ),
                    AJInput(
                        label: '标题',
                        hintText: '带清除按钮',
                        clearable: true
                    ),
                    AJInput(
                        key: inputKey,
                        label: '标题',
                        hintText: '自定义图标',
                        footer: Icon(AJIcons.info, size: 22)
                    ),
                    AJInput(
                        label: '验证码',
                        hintText: '请输入验证码',
                        footer: AJButton('获取验证码', size: AJButtonSize.mini, theme: AJButtonType.primary)
                    ),
                    AJInput(
                        label: '密码',
                        hintText: '请输入密码',
                        clearable: true,
                        obscureText: true
                    ),
                    AJInput(
                        hintText: '多行文本输入框...',
                        maxLines: 4
                    )
                  ]
              )
            ]
        )
    );
  }
}

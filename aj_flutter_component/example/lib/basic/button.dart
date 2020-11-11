import 'package:flutter/material.dart';

import '../router/sample.dart';
import '../other/title.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';

// 标题
class Title extends StatelessWidget {
  final String title;

  Title(this.title);

  @override
  Widget build(BuildContext context) {
    return Align(
        alignment: Alignment.topLeft,
        child: Container(
            padding: EdgeInsets.only(top: 10, bottom: 8),
            child: Text(title, style: TextStyle(
                fontSize: 16
            ))
        )
    );
  }
}

class ButtonExample extends StatefulWidget {
  @override
  _ButtonExampleState createState() => _ButtonExampleState();
}

class _ButtonExampleState extends State<ButtonExample> {

  // loading
  bool loading = true;
  bool disabled = true;

  toggleLoading() {
    setState(() {
      loading = !loading;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Sample(
      'Button',
      describe: '按钮',
      child: Container(
        child: Column(children: <Widget>[
          // 按钮
          Title('按钮'),
          AJButton('default'),
          box,
          AJButton(
              'primary',
              theme: AJButtonType.primary
          ),
          box,
          AJButton(
              'warn',
              theme: AJButtonType. warn
          ),
          box,
          Title('disabled 状态'),
          // 禁用状态
          AJButton('default 禁用状态', disabled: disabled),
          box,
          AJButton(
              'warn 禁用状态',
              theme: AJButtonType. warn,
              disabled: disabled
          ),
          box,
          AJButton(
              'primary 禁用状态',
              theme: AJButtonType.primary,
              disabled: disabled
          ),
          box,
          AJButton(
              'primary',
              theme: AJButtonType.primary,
              hollow: true,
              disabled: disabled
          ),
          box,

          Title('loading 状态'),
          AJButton('loading', loading: loading),

          box,
          AJButton(
            'loading',
            theme: AJButtonType.primary,
            loading: loading,
          ),
          box,
          AJButton(
              'loading',
              theme: AJButtonType. warn,
              loading: loading
          ),
          box,
          // box,
          Title('hollow'),
          // hollow
          AJButton(
              'default',
              hollow: true
          ),
          box,
          AJButton(
              'primary',
              theme: AJButtonType.primary,
              hollow: true
          ),
          box,
          AJButton(
              'warn',
              theme: AJButtonType. warn,
              hollow: true
          ),
          box,
          // mini
          Title('mini'),
          Row(
              children: <Widget>[
                AJButton('default', size: AJButtonSize.mini),
                rowBox,
                AJButton(
                    'primary',
                    theme: AJButtonType.primary,
                    size: AJButtonSize.mini
                ),
                rowBox,
                AJButton(
                    'warn',
                    theme: AJButtonType. warn,
                    size: AJButtonSize.mini
                )
              ]
          ),
          box,
          Title('mini loading'),
          Row(
              children: <Widget>[
                AJButton('default', size: AJButtonSize.mini, loading: true),
                rowBox,
                AJButton(
                    'primary',
                    theme: AJButtonType.primary,
                    size: AJButtonSize.mini,
                    loading: true
                ),
                rowBox,
                AJButton(
                    'warn',
                    theme: AJButtonType. warn,
                    size: AJButtonSize.mini,
                    loading: true
                )
              ]
          )
        ],),
      ),
    );
  }
}

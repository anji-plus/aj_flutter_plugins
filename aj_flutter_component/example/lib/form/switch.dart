import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class SwitchPage extends StatefulWidget {
  @override
  SwitchPageState createState() => SwitchPageState();
}

class SwitchPageState extends State {
  bool open = true;

  @override
  Widget build(BuildContext context) {
    return Sample(
        'Switch',
        describe: '滑动开关',
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextTitle('默认', noPadding: true),
              AJSwitch(),
              TextTitle('自定义size', noPadding: true),
              AJSwitch(
                  size: 20.0
              ),
              TextTitle('自定义颜色', noPadding: true),
              AJSwitch(
                color: Colors.red,
              ),
              TextTitle('禁用', noPadding: true),
              AJSwitch(
                disabled: true,
                checked: true,
              ),
              TextTitle('onChange', noPadding: true),
              AJSwitch(
                  onChange: (bool value) {
                    AJToast.info(context)(value ? 'open' : 'close');
                  }
              ),
              TextTitle('外部控制 - 受控', noPadding: true),
              AJSwitch(
                  checked: open,
                  onChange: (value) {
                    setState(() {
                      open = value;
                    });
                  }
              ),
              SizedBox(height: 10.0),
              AJButton(open ? '关闭' : '打开', theme: AJButtonType.primary, onClick: () {
                setState(() {
                  open = !open;
                });
              }),
              // AJButton('qrcode', theme: AJButtonType.primary, onClick: scan)
            ]
        )
    );
  }
}

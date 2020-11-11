import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';


class CheckboxPage extends StatefulWidget {
  @override
  CheckboxPageState createState() => CheckboxPageState();
}

final Widget child = Row(
  children: [
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJCheckbox(
        value: '1',
        child: Text('选项一'),
      ),
    ),
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJCheckbox(
        value: '2',
        child: Text('选项二'),
      ),
    ),
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJCheckbox(
        value: '3',
        child: Text('选项三'),
      ),
    ),
  ],
);

class CheckboxPageState extends State {
  List<String> value = [];

  @override
  Widget build(BuildContext context) {
    return Sample(
      'Checkbox',
      describe: '多选',
      child: Container(
        child: Column(
          children: <Widget>[
            TextTitle('默认', noPadding: true),
            AJCheckboxGroup(
              child: child,
            ),
            TextTitle('设置默认值', noPadding: true),
            AJCheckboxGroup(
              defaultValue: ['1'],
              child: child,
            ),
            TextTitle('onChange', noPadding: true),
            AJCheckboxGroup(
              child: child,
              onChange: (value) {
                AJToast.info(context)(value.toString());
              },
            ),
            TextTitle('受控组件', noPadding: true),
            AJCheckboxGroup(
              value: value,
              onChange: (data) {
                this.setState(() {
                  value = data;
                });
              },
              child: child,
            ),
            Padding(
              padding: EdgeInsets.only(top: 20),
              child: AJButton('设置第二个选中', onClick: () {
                this.setState(() {
                  value = ['2'];
                });
              }),
            ),
            TextTitle('禁用', noPadding: true),
            AJCheckboxGroup(
              defaultValue: ['2'],
              child: Row(
                children: [
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJCheckbox(
                      value: '1',
                      disabled: true,
                      child: Text('选项一'),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJCheckbox(
                      value: '2',
                      disabled: true,
                      child: Text('选项二'),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJCheckbox(
                      value: '3',
                      child: Text('选项三'),
                    ),
                  ),
                ],
              )
            ),
            TextTitle('选中数限制 - 最多选择两个', noPadding: true),
            AJCheckboxGroup(
              max: 2,
              child: child,
            ),
          ]
        )
      )
    );
  }
}

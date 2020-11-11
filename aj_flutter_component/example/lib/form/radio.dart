import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';
class RadioPage extends StatefulWidget {
  @override
  RadioPageState createState() => RadioPageState();
}

final Widget child = Row(
  children: [
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJRadio(
        value: '1',
        child: Text('选项一'),
      ),
    ),
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJRadio(
        value: '2',
        child: Text('选项二'),
      ),
    ),
    Padding(
      padding: EdgeInsets.only(right: 15),
      child: AJRadio(
        value: '3',
        child: Text('选项三'),
      ),
    ),
  ],
);

class RadioPageState extends State {
  String value;


  @override
  Widget build(BuildContext context) {
    return Sample(
      'Radio',
      describe: '单选',
      child: Container(
        child: Column(
          children: <Widget>[
            TextTitle('默认', noPadding: true),
            AJRadioGroup(
              child: child,
            ),
            TextTitle('设置默认值', noPadding: true),
            AJRadioGroup(
              defaultValue: '1',
              child: child,
            ),
            TextTitle('onChange', noPadding: true),
            AJRadioGroup(
              child: child,
              onChange: (value) {
                AJToast.info(context)(value.toString());
              },
            ),
            TextTitle('受控组件', noPadding: true),
            AJRadioGroup(
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
                  value = '2';
                });
              }),
            ),
            TextTitle('禁用', noPadding: true),
            AJRadioGroup(
              defaultValue: '2',
              child: Row(
                children: [
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJRadio(
                      value: '1',
                      disabled: true,
                      child: Text('选项一'),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJRadio(
                      value: '2',
                      disabled: true,
                      child: Text('选项二'),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(right: 15),
                    child: AJRadio(
                      value: '3',
                      child: Text('选项三'),
                    ),
                  ),
                ],
              )
            ),
          ]
        )
      )
    );
  }
}

import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class RadioListPage extends StatefulWidget {
  @override
  _RadioListState createState() => _RadioListState();
}

class _RadioListState extends State<RadioListPage> {
  String value;
  final List<AJRadiolistItem> options = [
    AJRadiolistItem(
      label: '选项一',
      value: '1'
    ),
    AJRadiolistItem(
      label: '选项二',
      value: '2'
    ),
    AJRadiolistItem(
      label: '选项三',
      value: '3'
    )
  ];
  final List<AJRadiolistItem> options2 = [
    AJRadiolistItem(
      label: '选项一',
      value: '1'
    ),
    AJRadiolistItem(
      label: '选项二 - 默认选中',
      value: '2'
    ),
    AJRadiolistItem(
      label: '选项三',
      value: '3'
    )
  ];
  final List<AJRadiolistItem> options3 = [
    AJRadiolistItem(
      label: '选项一 禁用',
      value: '1',
      disabled: true
    ),
    AJRadiolistItem(
      label: '选项二 - 默认选中',
      value: '2',
      disabled: true
    ),
    AJRadiolistItem(
      label: '选项二 - 默认选中禁用',
      value: '3'
    )
  ];
  @override
  Widget build(BuildContext context) {
    return Sample(
      'Radiolist',
      showPadding: false,
      describe: '单选列表',
      child: Column(
        children: <Widget>[
          TextTitle('单选列表'),
          AJRadiolist(
            children: options
          ),
          TextTitle('默认选中'),
          AJRadiolist(
            children: options2,
            defaultValue: '2'
          ),
          TextTitle('受控组件'),
          AJRadiolist(
            children: options,
            value: value,
            onChange: (checkedValue) {
              setState(() {
                value = checkedValue;
              });
            },
          ),
          Padding(
            padding: EdgeInsets.all(8),
            child: AJButton(
              '选中第二个',
              theme: AJButtonType.primary,
              onClick: () {
                setState(() {
                  value = '2';
                });
              }
            )
          ),
          TextTitle('禁用'),
          AJRadiolist(
            children: options3,
            defaultValue: '2'
          )
        ]
      )
    );
  }
}

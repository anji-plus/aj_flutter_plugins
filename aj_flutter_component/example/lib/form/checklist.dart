import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class ChecklistPage extends StatefulWidget {
  @override
  _ChecklistPageState createState() => _ChecklistPageState();
}

class _ChecklistPageState extends State {
  List<String> value = [];
  final List<AJChecklistItem> options = [
    AJChecklistItem(
      label: '选项一',
      value: '1'
    ),
    AJChecklistItem(
      label: '选项二',
      value: '2'
    ),
    AJChecklistItem(
      label: '选项三',
      value: '3'
    )
  ];
  final List<AJChecklistItem> options2 = [
    AJChecklistItem(
      label: '选项一 - 禁用',
      value: '1',
      disabled: true
    ),
    AJChecklistItem(
      label: '选项二 - 选中禁用',
      value: '2',
      disabled: true
    ),
    AJChecklistItem(
      label: '选项三',
      value: '3'
    ),
    AJChecklistItem(
      label: '选项四',
      value: '4'
    )
  ];

  @override
  Widget build(BuildContext context) {
    return Sample(
      'Checklist',
      showPadding: false,
      describe: '多选列表',
      child: Column(
        children: <Widget>[
          TextTitle('复选列表项'),
          AJChecklist(
            children: options
          ),
          // 又对齐
          TextTitle('右对齐'),
          AJChecklist(
            align: 'right',
            children: options
          ),
          // 受控组件
          TextTitle('受控组件'),
          AJChecklist(
            children: options,
            value: value,
            onChange: (checkedValue) {
              print(checkedValue);
              setState(() {
                value = checkedValue;
              });
            }
          ),
          Padding(
            padding: EdgeInsets.all(10),
            child: AJButton(
              value.indexOf('2') >= 0 ? '移除第二个' : '选中第二个',
              theme: AJButtonType.primary,
              onClick: () {
                setState(() {
                  if (value.indexOf('2') >= 0) {
                    value.remove('2');
                  } else {
                    value.add('2');
                  }
                });
              }
            )
          ),
          // 又对齐
          TextTitle('最多选择二个'),
          AJChecklist(
            max: 2,
            children: options
          ),
          // 禁用
          TextTitle('禁用'),
          AJChecklist(
            defaultValue: ['2'],
            children: options2
          )
        ]
      )
    );
  }
}

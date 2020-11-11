import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';

class CollapsePage extends StatefulWidget {
  @override
  CollapsePageState createState() => CollapsePageState();
}

class CollapsePageState extends State {
  @override
  Widget build(BuildContext context) {
    final List<AJCollapseItem> options = [];

    ['1', '2', '3'].forEach((item) {
      options.add(
          AJCollapseItem(
              title: Text('选项$item'),
              child: Text('内容$item')
          )
      );
    });

    return Sample(
        'Collapse',
        showPadding: false,
        describe: '折叠面板',
        child: Column(
            children: <Widget>[
              TextTitle('默认'),
              AJCollapse(
                  children: options
              ),
              box,
              box,
              TextTitle('设置默认展示'),
              AJCollapse(
                  defaultActive: ['1'],
                  children: options
              ),
              box,
              box,
              TextTitle('卡片方式'),
              AJCollapse(
                  card: true,
                  children: options
              ),
              box,
              box,
              TextTitle('手风琴模式'),
              AJCollapse(
                  accordion: true,
                  children: options
              ),
              box,
              box,
              TextTitle('onChange'),
              AJCollapse(
                  children: options,
                  onChange: (List<String> value) {
                    AJToast.info(context)('当前打开的：' + value.toString());
                  }
              )
            ]
        )
    );
  }
}
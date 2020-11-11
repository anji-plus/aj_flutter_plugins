import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class CallPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Cell',
        showPadding: false,
        describe: '列表',
        child: AJCells(
            children: [
              AJCell(
                  label: '标题文字'
              ),
              AJCell(
                  label: Text('手机号'),
                  content: Text('content')
              ),
              AJCell(
                  label: Row(
                      children: <Widget>[
                        Padding(
                            child: Icon(AJIcons.info),
                            padding: EdgeInsets.only(right: 2)
                        ),
                        Text('标题文字')
                      ]
                  ),
                  content: '带图标的标题'
              ),
              AJCell(
                  label: '标题文字',
                  content: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: <Widget>[
                        Padding(
                            child: Icon(AJIcons.info),
                            padding: EdgeInsets.only(right: 2)
                        ),
                        Text('带图标的内容')
                      ]
                  )
              ),
              AJCell(
                  label: '带icon',
                  content: '内容',
                  footer: Icon(AJIcons.clear)
              ),
              AJCell(
                  label: '标题文字',
                  content: '带点击效果的',
                  onClick: () {
                    print('click');
                  }
              ),
              AJCell(
                  label: '自定义内容',
                  content: Container(
                      child: AJButton('Button', size: AJButtonSize.mini)
                  )
              )
            ]
        )
    );
  }
}

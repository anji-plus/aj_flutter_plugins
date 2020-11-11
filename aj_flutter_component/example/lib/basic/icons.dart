
import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';

Widget _renderIcon (icon, { @required color, @required title, @required describe }) {
  return Padding(
      padding: EdgeInsets.only(bottom: 20),
      child: Row(
          children: <Widget>[
            Container(
              padding: EdgeInsets.only(right: 10),
              child: Icon(icon, size: 90, color: color),
            ),
            Expanded(
                flex: 1,
                child: Column(
                    children: <Widget>[
                      Align(
                        alignment: Alignment.topLeft,
                        child: Text(title, style: TextStyle(fontSize: 20)),
                      ),
                      Align(
                        alignment: Alignment.topLeft,
                        child: Text(describe, style: TextStyle(fontSize: 14, color: Color(0xff888888))),
                      )
                    ]
                )
            )
          ]
      )
  );
}

class AJIconsPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Icons',
        describe: '图标',
        child: Column(
            children: [
              // success
              _renderIcon(
                  AJIcons.success,
                  color: Color(0xff09BB07),
                  title: '成功',
                  describe: '用于表示操作顺利达成'
              ),
              // info
              _renderIcon(
                  AJIcons.info,
                  color: Color(0xff10AEFF),
                  title: '提示',
                  describe: '用于表示信息提示；也常用于缺乏条件的操作拦截，提示用户所需信息'
              ),
              // info
              _renderIcon(
                  AJIcons.info,
                  color: Color(0xffFFBE00),
                  title: '普通警告',
                  describe: '用于表示操作后将引起一定后果的情况'
              ),
              // info
              _renderIcon(
                  AJIcons.info,
                  color: Color(0xffF76260),
                  title: '强烈警告',
                  describe: '用于表示操作后将引起严重的不可挽回的后果的情况'
              ),
              // info
              _renderIcon(
                  AJIcons.loading,
                  color: Color(0xff10AEFF),
                  title: '等待',
                  describe: '用于表示等待'
              ),
              // info
              _renderIcon(
                  AJIcons.clear,
                  color: Color(0xfff0f0f0),
                  title: '删除',
                  describe: '用于表示删除'
              )
            ]
        )
    );
  }
}

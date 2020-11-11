import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';


class NotifyPage extends StatefulWidget {
  @override
  NotifyPageState createState() => NotifyPageState();
}

class NotifyPageState extends State {

  @override
  Widget build(BuildContext context) {
    return Sample(
        'AJNotify',
        describe: '通知',
        child: Column(
            children: [
              AJButton('success', theme: AJButtonType.primary, onClick: () {
                AJNotify.success(context)(
                    child: Text('我是通知内容, 可随意布局')
                );
              }),
              SizedBox(height: 20.0),
              AJButton('error', theme: AJButtonType.primary, onClick: () {
                AJNotify.error(context)(
                    child: Text('我是通知内容, 可随意布局')
                );
              }),
              SizedBox(height: 20.0),
              AJButton('自定义', theme: AJButtonType.primary, onClick: () {
                AJNotify.show(context)(
                    color: Colors.black,
                    child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Padding(
                              padding: EdgeInsets.only(
                                  right: 10
                              ),
                              child: Icon(AJIcons.info, color: Colors.white)
                          ),
                          Text('我是通知内容, 可自定义背景色和内容')
                        ]
                    )
                );
              })
            ]
        )
    );
  }
}

import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';

class DrawerPage extends StatefulWidget {
  @override
  DrawerPageState createState() => DrawerPageState();
}

class DrawerPageState extends State {
  void show(AJDrawerPlacement placement) {
    showAJDrawer(context)(
        placement: placement,
        child: Align(
            alignment: Alignment.center,
            child: Padding(
                padding: EdgeInsets.all(40.0),
                child: Text('我是内容, 可以随意自定义')
            )
        )
    );
  }

  @override
  Widget build(BuildContext context) {
    return Sample(
        'Drawer',
        describe: '抽屉',
        child: Column(
            children: [
              AJButton('top', theme: AJButtonType.primary, onClick: () {
                show(AJDrawerPlacement.top);
              }),
              SizedBox(height: 20.0),
              AJButton('right', theme: AJButtonType.primary, onClick: () {
                show(AJDrawerPlacement.right);
              }),
              SizedBox(height: 20.0),
              AJButton('bottom', theme: AJButtonType.primary, onClick: () {
                show(AJDrawerPlacement.bottom);
              }),
              SizedBox(height: 20.0),
              AJButton('left', theme: AJButtonType.primary, onClick: () {
                show(AJDrawerPlacement.left);
              }),
              SizedBox(height: 20.0),
              AJButton('无遮罩', theme: AJButtonType.primary, onClick: () {
                Function close;
                close = showAJDrawer(context)(
                    mask: false,
                    placement: AJDrawerPlacement.right,
                    child: SizedBox(
                        width: 300.0,
                        child: Align(
                            child: Padding(
                                padding: EdgeInsets.all(20.0),
                                child: AJButton('关闭', theme: AJButtonType.primary, onClick: () {
                                  close();
                                })
                            )
                        )
                    )
                );
              })
            ]
        )
    );
  }
}

//import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:aj_flutter_marquee/aj_flutter_marquee.dart';

class MarqueeWidgetDemo extends StatelessWidget{
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: AppBar(
          title: Text("跑马灯"),
        ),
        body:Container(
          color: Colors.blueGrey,
          height: 30,
          child: AJMarqueeWidget(
            text: "ListView即滚动列表控件，能将子控件组成可滚动的列表。当你需要排列的子控件超出容器大小",
            textStyle: TextStyle(
              fontSize: 16.0,
              color: Colors.red
            ),
            scrollAxis: Axis.horizontal,
          ),
        )
    );
  }
}

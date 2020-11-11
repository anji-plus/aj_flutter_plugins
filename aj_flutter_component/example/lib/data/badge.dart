import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class BadgePage extends StatefulWidget {
  @override
  BadgePageState createState() => BadgePageState();
}

class BadgePageState extends State {
  @override
  Widget build(BuildContext context) {
    return Sample(
      'Badge',
      describe: '徽章',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          TextTitle('默认', noPadding: true),
          Row(
            children: [
              AJBadge(child: '1'),
              Padding(
                padding: EdgeInsets.only(left: 8.0),
                child: AJBadge(child: 'value')
              )
            ]
          ),
          TextTitle('自定义样式', noPadding: true),
          AJBadge(
            child: 'Value',
            color: Colors.yellow,
            textStyle: TextStyle(
              fontSize: 14.0
            )
          ),
          TextTitle('边框', noPadding: true),
          AJBadge(
            child: 'Value',
            border: Border.all(width: 1, color: Colors.black)
          ),
          TextTitle('空心', noPadding: true),
          Row(
            children: [
              AJBadge(child: '1', hollow: true),
              Padding(
                padding: EdgeInsets.only(left: 8.0),
                child: AJBadge(child: 'value', hollow: true)
              )
            ]
          ),
        ]
      )
    );
  }
}

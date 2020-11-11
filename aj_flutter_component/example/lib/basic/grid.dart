import 'package:flutter/material.dart';
import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import '../other/title.dart';

class GridPage extends StatefulWidget {
  @override
  GridPageState createState() => GridPageState();
}

class GridPageState extends State {
  Widget itemBuilder (int i) {
    return SizedBox(
        height: 80.0,
        child: Align(
            alignment: Alignment.center,
            child: Text('Grid - $i')
        )
    );
  }

  @override
  Widget build(BuildContext context) {
    return Sample(
        'Grid',
        describe: '宫格',
        child: Column(
            children: <Widget>[
              TextTitle('九格宫'),
              AJGrid(
                  itemCount: 9,
                  itemBuilder: itemBuilder
              ),
              TextTitle('八格宫'),
              AJGrid(
                  count: 4,
                  itemCount: 8,
                  itemBuilder: itemBuilder
              ),
              TextTitle('自定义边框'),
              AJGrid(
                  itemCount: 9,
                  border: BorderSide(width: 1.0, color: Colors.red),
                  itemBuilder: itemBuilder
              )
            ]
        )
    );
  }
}

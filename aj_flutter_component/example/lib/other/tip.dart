import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';

class TipPage extends StatefulWidget {
  @override
  _TipPageState createState() => _TipPageState();
}

class _TipPageState extends State<TipPage> {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Tooltip',
        describe: '提示气泡框',
        child: Column(
            children: <Widget>[
              SizedBox(
                  height: 50,
                  child: AJTip(
                      content: Text('这是一个 tip'),
                      child: AJBadge(
                        child: 'top',
                      )
                  )
              ),
              SizedBox(
                  height: 50,
                  child: AJTip(
                      placement: AJTipPlacement.right,
                      content: Text('这是一个tip'),
                      child: AJBadge(
                        child: 'right',
                      )
                  )
              ),
              SizedBox(
                // height: 50,
                  child: AJTip(
                      placement: AJTipPlacement.bottom,
                      content: Text('这是一个 tip'),
                      child: AJBadge(
                        child: 'bottom',
                      )
                  )
              ),
              SizedBox(
                  height: 50,
                  child: AJTip(
                      placement: AJTipPlacement.left,
                      content: Text('这是一个 tip'),
                      child: AJBadge(
                        child: 'left',
                      )
                  )
              )
            ]
        )
    );
  }
}

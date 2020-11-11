import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:aj_flutter_auto_orientation/aj_flutter_auto_orientation.dart';

void main() {
  runApp(
    AutoOrientationDemo(),
  );
}

class AutoOrientationDemo extends StatefulWidget {
  AutoOrientationDemo({this.title = 'Auto Orientation Demo'});

  final String title;

  @override
  State<StatefulWidget> createState() {
    return _AutoOrientationDemoState();
  }
}

class _AutoOrientationDemoState extends State<AutoOrientationDemo> {
  TargetPlatform _platform;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: widget.title,
      theme: ThemeData.light().copyWith(
        platform: _platform ?? Theme.of(context).platform,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
        ),
        body: Column(
          children: <Widget>[
            Row(
              children: <Widget>[
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.landscapeRight,
                        DeviceOrientation.landscapeLeft,
                      ]);
                      AjFlutterAutoOrientation.landscapeLeftMode();
                    },
                    child: Padding(
                      child: Text("Landscape left mode only"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.landscapeRight,
                        DeviceOrientation.landscapeLeft,
                      ]);
                      AjFlutterAutoOrientation.landscapeRightMode();
                    },
                    child: Padding(
                      child: Text("Landscape right mode only"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
              ],
            ),

            Row(
              children: <Widget>[
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                      ]);
                      AjFlutterAutoOrientation.portraitUpMode();
                    },
                    child: Padding(
                      child: Text("Portrait up mode only"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                      ]);
                      AjFlutterAutoOrientation.portraitDownMode();
                    },
                    child: Padding(
                      child: Text("Portrait down mode only"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
              ],
            ),

            Row(
              children: <Widget>[
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                        DeviceOrientation.landscapeLeft,
                        DeviceOrientation.landscapeRight,
                      ]);
                      AjFlutterAutoOrientation.landscapeLeftMode();
                    },
                    child: Padding(
                      child: Text("Landscape left mode"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                        DeviceOrientation.landscapeLeft,
                        DeviceOrientation.landscapeRight,
                      ]);
                      AjFlutterAutoOrientation.landscapeRightMode();
                    },
                    child: Padding(
                      child: Text("Landscape right mode"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
              ],
            ),
            Row(
              children: <Widget>[
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                        DeviceOrientation.landscapeLeft,
                        DeviceOrientation.landscapeRight,
                      ]);
                      AjFlutterAutoOrientation.portraitUpMode();
                    },
                    child: Padding(
                      child: Text("Portrait up mode"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
                Expanded(
                  child: FlatButton(
                    onPressed: () {
                      SystemChrome.setPreferredOrientations([
                        DeviceOrientation.portraitUp,
                        DeviceOrientation.portraitDown,
                        DeviceOrientation.landscapeLeft,
                        DeviceOrientation.landscapeRight,
                      ]);
                      AjFlutterAutoOrientation.portraitDownMode();
                    },
                    child: Padding(
                      child: Text("Portrait down mode"),
                      padding: EdgeInsets.symmetric(vertical: 16.0),
                    ),
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}
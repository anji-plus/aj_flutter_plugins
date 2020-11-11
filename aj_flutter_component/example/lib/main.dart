import 'package:flutter/material.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import './index.dart';
import './router/router.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  AppState createState() => AppState();
}

class AppState extends State {
  bool isDefaultTheme = true;

  void toggleTheme() {
    this.setState(() {
      isDefaultTheme = !isDefaultTheme;
    });
  }

  @override
  Widget build(BuildContext context) {
    return AJUi(
        theme: isDefaultTheme ? AJTheme() : AJTheme(
            primaryColor: Color(0xff07C160),
            primaryColorDisabled: Color(0xff06AD56),
            warnColor: Color(0xfffb4343),
            warnColorDisabled: Color(0xfffaa7a3)
        ),
        config: AJConfig(
            toastSuccessDuration: 5000
        ),
        child: MaterialApp(
            debugShowCheckedModeBanner: false,
            routes: routes,
            theme: ThemeData(
                primaryColor: Color(0xffFFffff),
                platform: TargetPlatform.iOS //滑动返回
            ),
            home: Index(this.toggleTheme)
        )
    );
  }
}


import 'package:flutter/material.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'flutter之Hero动画',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          // Here we take the value from the MyHomePage object that was created by
          // the App.build method, and use it to set our appbar title.
          title: Text('flutter展示底部弹窗'),
          centerTitle: true,
        ),
        body: Center(
          child: RaisedButton(
            child: const Text('跳转'),
            onPressed: () {
              Navigator.push(context,
                  MaterialPageRoute<void>(builder: (BuildContext context) {
                return DialogPage();
              }));
            },
          ),
        ));
  }
}

class DialogPage extends StatefulWidget {
  @override
  _DialogPageState createState() => _DialogPageState();
}

class _DialogPageState extends State<DialogPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          // Here we take the value from the MyHomePage object that was created by
          // the App.build method, and use it to set our appbar title.
          title: Text('flutter展示底部弹窗'),
          centerTitle: true,
        ),
        body: Center(
          child: RaisedButton(
            child: const Text('展示底部弹窗'),
            onPressed: () {
              showModalBottomSheet<void>(
                  context: context,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(20),
                      topRight: Radius.circular(20),
                    ),
                  ),
                  builder: (BuildContext context) {
                    return Container(
                      height: 400,
                      child: Column(
                        children: <Widget>[
                          Row(
                            children: <Widget>[
                              Expanded(child: Container(height: 40,)),
                              InkWell(
                                onTap: (){
                                  Navigator.pop(context);
                                },
                                child: Container(
                                  margin: EdgeInsets.only(right: 20),
                                  child: Icon(
                                    Icons.close,
                                    color: Colors.blue,
                                  ),
                                ),
                              )
                            ],
                          ),
                          Expanded(
                              child: Container(
                            color: Colors.amberAccent,
                          )),
                        ],
                      ),
                    );
                  });
            },
          ),
        ));
  }
}

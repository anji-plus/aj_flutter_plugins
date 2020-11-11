import 'package:flutter/material.dart';

final Widget box = Container(
  height: 10,
);

final Widget rowBox = Container(
  width: 10,
);

class TextTitle extends StatelessWidget {
  final String title;
  final bool noPadding;
  final bool needBlod;

  TextTitle(this.title, {this.noPadding = false, this.needBlod = true});

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: noPadding
            ? EdgeInsets.only(top: 30.0, bottom: 8.0)
            : EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 8.0),
        margin: EdgeInsets.only(bottom: 20),
        child: Align(
            alignment: Alignment.topLeft,
            child: Text(title,
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: needBlod ? FontWeight.bold : FontWeight.w300
                ))));
  }
}

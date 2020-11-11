import 'package:flutter/material.dart';
import '../../others/cell/cell.dart';

class AJForm extends StatefulWidget {
  final bool boxBorder;
  final double spacing;
  final double height;
  final List<Widget> children;

  AJForm({
    key,
    this.boxBorder = true,
    this.spacing = labelSpacing,
    this.height = 50,
    @required this.children
  }) : super(key: key);

  static AJFormState of(BuildContext context) {
    final AJFormScope scope = context.inheritFromWidgetOfExactType(AJFormScope);
    return scope?.state;
  }

  @override
  AJFormState createState() => AJFormState();
}

class AJFormState extends State<AJForm> {
  final Map<dynamic, dynamic> formValue = {};

  // 设置表单值
  void setValue(Map<dynamic, dynamic> value) {
    setState(() {
      formValue.addAll(value);
    });
  }

  validate() {
  }

  @override
  Widget build(BuildContext context) {
    return AJFormScope(
        state: this,
        formValue: formValue,
        child: AJCells(
            boxBorder: widget.boxBorder,
            spacing: widget.spacing,
            children: widget.children
        )
    );
  }
}

class AJFormScope extends InheritedWidget {
  final AJFormState state;
  final formValue;

  AJFormScope({
    Key key,
    this.state,
    this.formValue,
    Widget child
  }) : super(key: key, child: child);

  static AJFormScope of(BuildContext context) {
    return context.inheritFromWidgetOfExactType(AJFormScope);
  }

  //是否重建widget就取决于数据是否相同
  @override
  bool updateShouldNotify(AJFormScope oldWidget) {
    return formValue != oldWidget.formValue;
  }
}
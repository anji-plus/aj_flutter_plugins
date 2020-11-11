import 'package:flutter/material.dart';
import '../../theme/aj_theme.dart';
import '../../others/animation/rotating.dart';

class AJGrid extends StatefulWidget {
  final int itemCount;
  final Widget Function(int index) itemBuilder;
  final int count;
  final BorderSide border;

  AJGrid({
    @required this.itemCount,
    @required this.itemBuilder,
    this.count = 3,
    this.border
  });

  @override
  AJGridState createState() => AJGridState();
}

class AJGridState extends State<AJGrid> {
  GlobalKey _boxKey = GlobalKey();
  double _itemWidth = 0.0;
  // 边框
  BorderSide border;

  AJGridState() {
    WidgetsBinding.instance.addPostFrameCallback(init);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // 边框
    final Color borderColor = AJUi.getTheme(context).defaultBorderColor;
    final BorderSide _defaultBorder = BorderSide(width: 0.8, color: borderColor);
    border = widget.border == null ? _defaultBorder : widget.border;
  }

  void init(time) {
    setState(() {
      _itemWidth = _boxKey.currentContext.size.width / widget.count;
    });
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> children = [];
    // 计算行数
    final int rowNum = (widget.itemCount / widget.count).ceil();

    // 行数
    for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
      final List<Widget> list = [];

      // 列数
      for (int index = 0; index < widget.count; index++) {
        final int eq = (rowIndex * widget.count) + index;
        if (eq <= widget.itemCount - 1) {
          list.add(SizedBox(
              width: _itemWidth,
              child: DecoratedBox(
                  decoration: BoxDecoration(
                      color: Colors.white,
                      border: Border(
                          top: rowIndex == 0 ? border : BorderSide.none,
                          right: border,
                          bottom: border,
                          left: index == 0 ? border : BorderSide.none
                      )
                  ),
                  child: widget.itemBuilder((rowIndex * widget.count) + index)
              )
          ));
        }
      }

      children.add(Row(
          children: list
      ));
    }

    return SizedBox(
        key: _boxKey,
        child: Column(
            children: children
        )
    );
  }
}

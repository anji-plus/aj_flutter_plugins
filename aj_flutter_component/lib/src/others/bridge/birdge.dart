import '../../theme/aj_theme.dart';
import 'package:flutter/material.dart';


class AJBadge extends StatefulWidget {
  final child;
  final Color color;
  final TextStyle textStyle;
  final Border border;
  final bool hollow;

  AJBadge({
    this.child,
    this.color,
    this.textStyle,
    this.border,
    this.hollow = false
  });

  @override
  AJBadgeState createState() => AJBadgeState();
}

class AJBadgeState extends State<AJBadge> {
  Color color;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final AJTheme theme = AJUi.getTheme(context);
    color = widget.color == null ? theme.warnColor : widget.color;
  }

  @override
  Widget build(BuildContext context) {
    Color textColor;
    Color boxColor;
    Border border;

    if (widget.hollow) {
      textColor = color;
      boxColor = Colors.transparent;
      border = Border.all(width: 1, color: color);
    } else {
      textColor = Colors.white;
      boxColor = color;
      border = null;
    }

    return Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          DecoratedBox(
              decoration: BoxDecoration(
                  color: boxColor,
                  borderRadius: BorderRadius.all(Radius.circular(20.0)),
                  border: widget.border == null ? border : widget.border
              ),
              child: Padding(
                  padding: EdgeInsets.only(top: 2.0, right: 7.0, bottom: 2.0, left: 7.0),
                  child: DefaultTextStyle(
                      style: TextStyle(
                          fontSize: 13.0,
                          color: textColor
                      ),
                      child: Text(widget.child, style: widget.textStyle)
                  )
              )
          )
        ]
    );
  }
}

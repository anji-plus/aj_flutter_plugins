import 'package:flutter/material.dart';
import '../../theme/aj_theme.dart';
import '../../others/util/utils.dart';
import 'notify_widget.dart';

typedef _Notify = Function ({
int duration,
@required Widget child
});

typedef _Show = Function ({
Color color,
int duration,
@required Widget child
});

class AJNotify {
  // 成功
  static _Notify success (BuildContext context) {
    final int notifySuccessDuration = AJUi.getConfig(context).notifySuccessDuration;
    return ({ duration, child }) {
      AJNotify.show(context)(
          color: AJUi.getTheme(context).primaryColor,
          duration: duration == null ? notifySuccessDuration : duration,
          child: child
      );
    };
  }

  // 失败
  static error (BuildContext context) {
    final int notifyErrorDuration = AJUi.getConfig(context).notifyErrorDuration;
    return ({ duration, child }) {
      AJNotify.show(context)(
          color: AJUi.getTheme(context).warnColor,
          duration: duration == null ? notifyErrorDuration : duration,
          child: child
      );
    };
  }

  static _Show show (BuildContext context) {
    final int notifyDuration = AJUi.getConfig(context).notifyDuration;
    return ({ color, duration, child }) {
      final GlobalKey widgetKey = GlobalKey();
      final Function remove = createOverlayEntry(
          context: context,
          child: NotifyWidget(
              key: widgetKey,
              color: color == null ? AJUi.getTheme(context).warnColor : color,
              child: child
          )
      );

      void close() async {
        // 反向执行动画
        await (widgetKey.currentState as NotifyWidgetState).reverseAnimation();
        remove();
      }

      Future.delayed(Duration(milliseconds: duration == null ? notifyDuration : duration), close);
    };
  }
}
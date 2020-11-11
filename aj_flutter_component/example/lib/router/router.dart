import 'package:flutter/material.dart';
import '../basic/button.dart';
import '../basic/icons.dart';
import '../basic/cell.dart';
import '../basic/grid.dart';
import '../other/tip.dart';
import '../other/toast.dart';
import '../other/collapse.dart';
import '../other/marquee.dart';
import '../other/dialog.dart';
import '../other/actionsheet.dart';
import '../notice/notify.dart';
import '../other/drawer.dart';
import '../data/progress.dart';
import '../data/badge.dart';
import '../form/checkbox.dart';
import '../form/checklist.dart';
import '../form/radio.dart';
import '../form/radiolist.dart';
import '../form/input.dart';
import '../form/switch.dart';
import '../form/silder.dart';


final routes = <String, WidgetBuilder>{
/*basic*/
//Layout 布局
  '/button': (_) => ButtonExample(),
  '/icons': (_) => AJIconsPage(),
  '/cell': (_) => CallPage(),
  '/grid': (_) => GridPage(),
//Form
  '/checkbox': (_) => CheckboxPage(),
  '/checklist': (_) => ChecklistPage(),
  '/radio': (_) => RadioPage(),
  '/radiolist': (_) => RadioListPage(),
  '/input': (_) => InputPage(),
  '/switch': (_) => SwitchPage(),
  '/silder': (_) => SliderPage(),


//data
  '/progress': (_) => ProgressPage(),
  '/badge': (_) => BadgePage(),

//Notice
  '/toast': (_) => ToastPage(),
  '/notify': (_) => NotifyPage(),

//Others
  '/drawer': (_) => DrawerPage(),
  '/dialog': (_) => DialogPage(),
  '/actionsheet': (_) => ActionsheetPage(),

  '/Tooltip': (_) => TipPage(),

  '/marquee': (_) => MarqueePage(),

  '/collapse': (_) => CollapsePage(),
};

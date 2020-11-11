import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';

final Widget child = Text('请注意：这是一条通知, 通知，通知，通知，通知，通知，通知，通知，通知，通知...');

class MarqueePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Marquee',
        describe: '通告栏',
        showPadding: false,
        child: Column(
            children: [
              TextTitle('默认'),
              AJMarquee(
                  child: child
              ),
              TextTitle('默认'),
              AJMarquee(
                  child: Text('长度不够, 不滚动...')
              ),
              TextTitle('带图标'),
              AJMarquee(
                  icon: Icon(Icons.ac_unit, color: Color(0xffed6a0c), size: 18.0),
                  child: child
              ),
              TextTitle('可关闭'),
              AJMarquee(
                  closeable: true,
                  child: child
              ),
              TextTitle('自定义widget'),
              AJMarquee(
                  child: Row(
                      children: [
                        Icon(Icons.ac_unit, color: Color(0xffed6a0c), size: 18.0),
                        Icon(Icons.ac_unit, color: Color(0xffed6a0c), size: 18.0),
                        Text('自定义widget'),
                        child
                      ]
                  )
              ),
              TextTitle('自定义颜色和背景'),
              AJMarquee(
                  child: child,
                  fontColor: Color(0xffffffff),
                  color: Color(0xffF64E37)
              ),
            ]
        )
    );
  }
}
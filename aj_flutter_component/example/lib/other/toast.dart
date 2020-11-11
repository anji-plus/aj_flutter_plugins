import 'package:flutter/material.dart';
import '../router/sample.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';
import 'title.dart';

final Widget rowBox = SizedBox(height: 15);

class ToastPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Sample(
        'Toast',
        describe: '弹出式提示',
        child: Column(
            children: <Widget>[
              TextTitle('Toast.info', noPadding: true),
              AJButton('default', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)('提示');
              }),
              rowBox,
              AJButton('info top', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)('提示 - top', align: AJToastInfoAlign.top);
              }),
              rowBox,
              AJButton('info bottom', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)('提示 - bottom', align: AJToastInfoAlign.bottom);
              }),
              rowBox,
              AJButton('info 自定义时间', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)('5秒消失...', duration: 5000);
              }),
              rowBox,
              AJButton('自定义距离', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)(
                    '只适合align为top或bottom...',
                    align: AJToastInfoAlign.top,
                    distance: 250.0
                );
              }),
              rowBox,
              AJButton('带Widget的内容', theme: AJButtonType.primary, onClick: () {
                AJToast.info(context)(
                    Row(
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          Icon(AJIcons.loading, color: Colors.white),
                          Padding(
                              padding: EdgeInsets.only(left: 10),
                              child: Text('任意内容...')
                          )
                        ]
                    )
                );
              }),
              rowBox,
              TextTitle('其他提示', noPadding: true),
              AJButton('loading', theme: AJButtonType.primary, onClick: () {
                final Function close = AJToast.loading(context)(
                    message: '加载中...'
                );
                // 五秒后关闭
                Future.delayed(Duration(milliseconds: 5000), close);
              }),
              rowBox,
              AJButton('loading - 无文字', theme: AJButtonType.primary, onClick: () {
                AJToast.loading(context)(
                    duration: 3000
                );
              }),
              rowBox,
              AJButton('success', theme: AJButtonType.primary, onClick: () {
                AJToast.success(context)(
                    message: '已完成'
                );
              }),
              rowBox,
              AJButton('success - 无文字', theme: AJButtonType.primary, onClick: () {
                AJToast.success(context)();
              }),
              rowBox,
              AJButton('fail', theme: AJButtonType.primary, onClick: () {
                AJToast.fail(context)(
                    message: '操作失败'
                );
              }),
              rowBox,
              AJButton('fail - 无文字', theme: AJButtonType.primary, onClick: () {
                AJToast.fail(context)();
              }),
            ]
        )
    );
  }
}
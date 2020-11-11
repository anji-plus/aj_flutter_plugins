import 'package:flutter/material.dart';
import 'package:aj_flutter_component/aj_flutter_component.dart';

class Index extends StatefulWidget {
  final Function toggleTheme;
  Index(this.toggleTheme);

  @override
  _IndexState createState() => _IndexState();
}

class _IndexState extends State<Index> {
  // 标题padding
  final double titlePadding = 36.0;
  // 列表左右padding
  final double listPadding = 18.0;

  bool isDefault = true;

  // 列表
  final list = [
    {
      'title': 'Basic',
      'icon': 'assets/images/index-icon/icon_nav_layout.png',
      'childer': [
        {
          'title': 'Layout',
          'url': null
        },
        {
          'title': 'cell',
          'url': '/cell'
        },
        {
          'title': 'grid',
          'url': '/grid'
        },
        {
          'title': 'Container 布局',
          'url': null
        },
        {
          'title': 'Color 色彩',
          'url': null
        },
        {
          'title': 'Typography 字体',
          'url': null
        },
        {
          'title': 'Icon 图标',
          'url': '/icons'
        },
        {
          'title': 'Button 按钮',
          'url': '/button'
        },
      ]
    },
    {
      'title': 'Form',
      'icon': 'assets/images/index-icon/icon_nav_form.png',
      'childer': [
        {
          'title': 'Radio 单选框',
          'url': '/radio'
        },
        {
          'title': 'radiolist 单选框',
          'url': '/radiolist'
        },
        {
          'title': 'Checkbox 多选框',
          'url': '/checkbox'
        },
        {
          'title': 'checklist 多选框',
          'url': '/checklist'
        },
        {
          'title': 'Input 输入框',
          'url': '/input'
        },
        {
          'title': 'InputNumber 计数器',
          'url': null
        },
        {
          'title': 'Select 选择器',
          'url': null
        },
        {
          'title': 'Cascader 级联选择器',
          'url': null
        },
        {
          'title': 'Switch 开关',
          'url': '/switch'
        },
        {
          'title': 'Slider 滑块',
          'url': '/silder'
        },
        {
          'title': 'TimePicker 时间选择器',
          'url': null
        },
        {
          'title': 'DatePicker 日期选择器',
          'url': null
        },
        {
          'title': 'DateTimePicker 日期时间选择器',
          'url': null
        },
        {
          'title': 'Upload 上传',
          'url': null
        },
        {
          'title': 'Rate 评分',
          'url': null
        },
        {
          'title': 'ColorPicker 颜色选择器',
          'url': null
        },
        {
          'title': 'Transfer 穿梭框',
          'url': null
        },
        {
          'title': 'Form 表单',
          'url': null
        },
      ]
    },
    {
      'title': 'Data',
      'icon': 'assets/images/index-icon/icon_nav_layout.png',
      'childer': [
        {
          'title': 'Table 表格',
          'url': null
        },
        {
          'title': 'Tag 标签',
          'url': null
        },
        {
          'title': 'Progress 进度条',
          'url': '/progress'
        },
        {
          'title': 'Tree 树形控件',
          'url': null
        },
        {
          'title': 'Pagination 分页',
          'url': null
        },
        {
          'title': 'Badge 标记',
          'url': '/badge'
        },
      ]
    },
    {
      'title': 'Notice',
      'icon': 'assets/images/index-icon/icon_nav_form.png',
      'childer': [
        {
          'title': 'Alert 警告',
          'url': '/toast'
        },
        {
          'title': 'Loading 加载',
          'url': '/toast'
        },
        {
          'title': 'toast 消息提示',
          'url': '/toast'
        },
        {
          'title': 'MessageBox 弹框',
          'url': '/dialog'
        },
        {
          'title': 'Notification 通知',
          'url': '/notify'
        },
      ]
    },
    {
      'title': 'Navigation',
      'icon': 'assets/images/index-icon/icon_nav_layout.png',
      'childer': [
        {
          'title': 'NavMenu 导航菜单',
          'url': null
        },
        {
          'title': 'Tabs 标签页',
          'url': null
        },
        {
          'title': 'Breadcrumb 面包屑',
          'url': null
        },
        {
          'title': 'Dropdown 下拉菜单',
          'url': null
        },
        {
          'title': 'Steps 步骤条',
          'url': null
        },
      ]
    },
    {
      'title': 'Others',
      'icon': 'assets/images/index-icon/icon_nav_form.png',
      'childer': [
        {
          'title': 'Dialog 对话框',
          'url': '/dialog'
        },
        {
          'title': 'Actiopnsheet 对话框',
          'url': '/actionsheet'
        },
        {
          'title': 'Tooltip 文字提示',
          'url': '/Tooltip'
        },
        {
          'title': 'Popover 弹出框',
          'url': null
        },
        {
          'title': 'Card 卡片',
          'url': null
        },
        {
          'title': 'Marquee 跑马灯',
          'url': '/marquee'
        },
        {
          'title': 'Collapse 折叠面板',
          'url': '/collapse'
        },
        {
          'title': 'drawer 面板',
          'url': '/drawer'
        },
      ]
    },
  ];


  void toggleTheme() {
    widget.toggleTheme();
    this.setState(() {
      isDefault = !isDefault;
      AJToast.info(context)('当前为' + (isDefault ? '默认' : '自定义') + '主题');
    });
  }

  @override
  Widget build(BuildContext context) {

    final List<AJCollapseItem> children = [];
    final theme = AJUi.getTheme(context);

    list.forEach((item) {
      children.add(
          AJCollapseItem(
              title: Text(item['title'], style: TextStyle(
                  fontSize: 16.0
              )),
              child: Column(
                  children: renderSubItem(item['childer'])
              )
          )
      );
    });


    return Scaffold(
        backgroundColor: Color(0xfff8f8f8),
        // floatingActionButton: FloatingActionButton(
        //   onPressed: scan,
        //   backgroundColor: theme.primaryColor,
        //   child: Icon(IconData(0xe618, fontFamily: 'iconfont'))
        // ),
        body: ListView(
            children: [
              // head
              Stack(
                  children: [
                    Container(
                        padding: EdgeInsets.all(36.0),
                        child: Column(
                            children: [
                              Row(
                                  children: [
                                    Text('AJComponent', style: TextStyle(
                                        fontSize: 25.0
                                    ))
                                  ]
                              ),
                              Container(
                                  padding: EdgeInsets.only(top: 10.0),
                                  child: Text('AJComponent 是一套同微信原生视觉体验一致的基础样式库，旨在统一Web和APP的组件，整体目录按照Element UI设计 。', style: TextStyle(
                                      fontSize: 15.0,
                                      color: Color(0xff888888)
                                  ))
                              )
                            ]
                        )
                    ),
                    Positioned(
                        top: 10,
                        right: 20,
                        child: AJButton(
                          isDefault ? '默认主题' : '自定义主题',
                          size: AJButtonSize.mini,
                          onClick: toggleTheme,
                        )
                    )
                  ]
              ),
              Container(
                  padding: EdgeInsets.only(left: 18.0, right: 18.0, bottom: 10.0),
                  child: AJCollapse(
                      card: true,
                      accordion: true,
                      buildTitle: buildTitle,
                      buildContent: buildContent,
                      children: children
                  )
              )
            ]
        )
    );
  }


  // 渲染二级列表
  List<Widget> renderSubItem(subList) {
    final List<Widget> widgetList = [];

    // 循环数组
    subList.forEach((dynamic item) {
      final List<Widget> content = [
        Container(
            padding: EdgeInsets.only(
                top: 16.0,
                bottom: 16.0
            ),
            child: Row(
                children: <Widget>[
                  Expanded(
                      flex: 1,
                      child: Text(item['title'], style: TextStyle(
                          fontSize: 16.0
                      ))
                  ),
                  Container(
                      child: Image.asset('assets/images/right-icon.png', height: 16.0)
                  )
                ]
            )
        )
      ];

      // 第一个不添加边框
      if (subList.indexOf(item) > 0) {
        content.insert(0, Divider(height: 1, color: Color(0xffd8d8d8)));
      }

      widgetList.add(
          InkWell(
              onTap: () {
                if (item['url'] == null) {
                  AJToast.info(context)('正在努力开发中...', duration: 1500);
                  return;
                }
                Navigator.of(context).pushNamed(item['url']);
              },
              child: Padding(
                  padding: EdgeInsets.only(
                      left: listPadding,
                      right: listPadding
                  ),
                  child: Column(
                      children: content
                  )
              )
          )
      );
    });

    return widgetList;
  }

  // 渲染标题
  Widget buildTitle(bool checked, int index, Widget child) {
    return Opacity(
        opacity: checked ? 0.5 : 1.0,
        child: Padding(
            padding: EdgeInsets.only(
                top: 25.0,
                right: listPadding,
                bottom: 25.0,
                left: listPadding
            ),
            child: Row(
                children: <Widget>[
                  // 名称
                  Expanded(
                      flex: 1,
                      child: child
                  ),
                  Image.asset(list[index]['icon'], height: 22.0)
                ]
            )
        )
    );
  }

  // 渲染内容
  Widget buildContent(bool checked, int index, Widget child) {
    return child;
  }


}

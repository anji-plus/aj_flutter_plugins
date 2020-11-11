import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:aj_flutter_auto_orientation/aj_flutter_auto_orientation.dart';

void main() {
  const MethodChannel channel = MethodChannel('aj_flutter_auto_orientation');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('landscapeLeftMode', () async {
    expect(await AjFlutterAutoOrientation.landscapeRightMode(), '42');
  });
  test('landscapeLeftMode', () async {
    expect(await AjFlutterAutoOrientation.landscapeLeftMode(), '42');
  });
  test('portraitUpMode', () async {
    expect(await AjFlutterAutoOrientation.portraitUpMode(), '42');
  });
  test('portraitDownMode', () async {
    expect(await AjFlutterAutoOrientation.portraitDownMode(), '42');
  });
}

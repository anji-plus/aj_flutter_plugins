import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:aj_flutter_scan/aj_flutter_scan.dart';

void main() {
  const MethodChannel channel = MethodChannel('aj_flutter_scan');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
//    expect(await AjFlutterScan.platformVersion, '42');
  });
}

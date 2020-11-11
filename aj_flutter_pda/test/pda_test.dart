import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pda/aj_flutter_pda.dart';

void main() {
  const MethodChannel channel = MethodChannel('pda');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
//    expect(await Pda.platformVersion, '42');
  });
}

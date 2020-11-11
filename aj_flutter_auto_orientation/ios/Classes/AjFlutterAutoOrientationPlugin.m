#import "AjFlutterAutoOrientationPlugin.h"

@implementation AjFlutterAutoOrientationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"aj_flutter_auto_orientation"
            binaryMessenger:[registrar messenger]];
  AjFlutterAutoOrientationPlugin* instance = [[AjFlutterAutoOrientationPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"setLandscapeRight" isEqualToString:call.method]) {
      [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeRight) forKey:@"orientation"];
    }

    if ([@"setLandscapeLeft" isEqualToString:call.method]) {
        [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeLeft) forKey:@"orientation"];
      }

    if ([@"setPortraitUp" isEqualToString:call.method]) {
      [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationPortrait) forKey:@"orientation"];
    }

    if ([@"setPortraitDown" isEqualToString:call.method]) {
      [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationPortraitUpsideDown) forKey:@"orientation"];
    }

    result(FlutterMethodNotImplemented);
}

@end

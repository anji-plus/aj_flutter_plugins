#import "AjFlutterUpdatePlugin.h"

@implementation AjFlutterUpdatePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"aj_flutter_update"
            binaryMessenger:[registrar messenger]];
  AjFlutterUpdatePlugin* instance = [[AjFlutterUpdatePlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"launchUrl" isEqualToString:call.method]) {
        NSDictionary *arguments = [call arguments];
        NSString *utlString = arguments[@"url"];
        [self launchURL:utlString result:result];
    } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)launchURL:(NSString *)urlString result:(FlutterResult)result {
    NSURL *url = [NSURL URLWithString:urlString];
    if([[UIApplication sharedApplication] canOpenURL:url]){
        [[UIApplication sharedApplication] openURL:url];
    }
    result(@"");
}

- (BOOL)canLaunchURL:(NSString *)urlString {
  NSURL *url = [NSURL URLWithString:urlString];
  UIApplication *application = [UIApplication sharedApplication];
  return [application canOpenURL:url];
}

@end

#import "AjFlutterPlugin.h"
#import <WebKit/WebKit.h>


@implementation AjFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel methodChannelWithName:@"aj_flutter_plugin" binaryMessenger:[registrar messenger]];
  AjFlutterPlugin* instance = [[AjFlutterPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
      result(@{
               @"appName" : [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"]
               ?: [NSNull null],
               @"packageName" : [[NSBundle mainBundle] bundleIdentifier] ?: [NSNull null],
               @"version" : [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"]
               ?: [NSNull null],
               @"buildNumber" : [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]
               ?: [NSNull null],
               });
  } else if ([@"launchUrl" isEqualToString:call.method]) {
      NSDictionary *arguments = [call arguments];
      NSString *utlString = arguments[@"url"];
      [self launchURL:utlString result:result];
    
  } else if ([@"canLaunch" isEqualToString:call.method]) {
      NSString *url = call.arguments[@"url"];
      result(@([self canLaunchURL:url]));
  } else if ([@"exitAppMethod" isEqualToString:call.method]) {
      result([NSNumber numberWithBool:true]);
      exit(0);
  } else if ([@"clearWebCache" isEqualToString:call.method]) {
          result([NSNumber numberWithBool:[self clearWebCache]]);
  } else if ([@"isiOSSimuLator" isEqualToString:call.method]) {
      result([NSNumber numberWithBool:[self isiOSSimuLator]]);
  } else if ([@"locationPermissions" isEqualToString:call.method]) {
//      这里就要查看CLLocationManager的授权状态，此方法会返回当前授权状态：
//      [CLLocationManager authorizationStatus];
//      授权状态为枚举值：
//      kCLAuthorizationStatusNotDetermined                  //用户尚未对该应用程序作出选择
//      kCLAuthorizationStatusRestricted                     //应用程序的定位权限被限制
//      kCLAuthorizationStatusAuthorizedAlways               //一直允许获取定位
//      kCLAuthorizationStatusAuthorizedWhenInUse            //在使用时允许获取定位
//      kCLAuthorizationStatusAuthorized                     //已废弃，相当于一直允许获取定位
//      kCLAuthorizationStatusDenied
      if ([CLLocationManager locationServicesEnabled] && ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorizedWhenInUse || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusAuthorizedAlways)) {
          //定位功能可用 1
          result([NSNumber numberWithInteger:1]);
      } else if([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined){

          //未选择权限 2
          result([NSNumber numberWithInteger:2]);
      }else{
          //定位不能用 0
          result([NSNumber numberWithInteger:0]);
      }
  }
//  else if ([@"requestlocationAuthorization" isEqualToString:call.method]) {
////      self.result = result;
//      CLLocationManager *lcManager = [[CLLocationManager alloc]init];
//      [lcManager requestWhenInUseAuthorization];
//      result([NSNumber numberWithInteger:1]);
//
////      lcManager.delegate = self;
//  }
  else {
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

//判断是否是模拟器
-(BOOL)isiOSSimuLator {
    if (TARGET_IPHONE_SIMULATOR == 1 && TARGET_OS_IPHONE == 1) {
        //模拟器
        return YES;
    }else{
        //真机
        return NO;
    }
}

- (BOOL)clearWebCache{
    if (@available(iOS 9.0, *)) {
        NSArray * types =@[WKWebsiteDataTypeMemoryCache,WKWebsiteDataTypeDiskCache]; // iOS 9.0之后
        NSSet *websiteDataTypes = [NSSet setWithArray:types];
        NSDate *dateFrom = [NSDate dateWithTimeIntervalSince1970:0];
        [[WKWebsiteDataStore defaultDataStore] removeDataOfTypes:websiteDataTypes modifiedSince:dateFrom completionHandler:^{

        }];
    } else {
        NSString *libraryDir = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory,NSUserDomainMask, YES)[0];

        NSString *bundleId = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"];

        NSString *webkitFolderInLib = [NSString stringWithFormat:@"%@/WebKit",libraryDir];

        NSString *webKitFolderInCaches = [NSString stringWithFormat:@"%@/Caches/%@/WebKit",libraryDir,bundleId];

        NSError *error;

        [[NSFileManager defaultManager] removeItemAtPath:webKitFolderInCaches error:&error];

        [[NSFileManager defaultManager] removeItemAtPath:webkitFolderInLib error:nil];

        [[NSURLCache sharedURLCache] removeAllCachedResponses];

        [[NSURLCache sharedURLCache] setDiskCapacity:0];

        [[NSURLCache sharedURLCache] setMemoryCapacity:0];

    }
    return true;
}

/** 定位服务状态改变时调用*/
//-(void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
//{
//    switch (status) {
//        case kCLAuthorizationStatusNotDetermined:
//        {
//            NSLog(@"用户还未决定授权");
//            self.result([NSNumber numberWithInteger:2]);
//            break;
//        }
//        case kCLAuthorizationStatusRestricted:
//        {
//            NSLog(@"访问受限");
//            self.result([NSNumber numberWithInteger:1]);
//            break;
//        }
//        case kCLAuthorizationStatusDenied:
//        {
//            // 类方法，判断是否开启定位服务
//            if ([CLLocationManager locationServicesEnabled]) {
//                NSLog(@"定位服务开启，被拒绝");
//                self.result([NSNumber numberWithInteger:0]);
//
//            } else {
//                NSLog(@"定位服务关闭，不可用");
//                self.result([NSNumber numberWithInteger:0]);
//            }
//            break;
//        }
//        case kCLAuthorizationStatusAuthorizedAlways:
//        {
//            NSLog(@"获得前后台授权");
//            self.result([NSNumber numberWithInteger:1]);
//            break;
//        }
//        case kCLAuthorizationStatusAuthorizedWhenInUse:
//        {
//            NSLog(@"获得前台授权");
//            self.result([NSNumber numberWithInteger:1]);
//            break;
//        }
//        default:
//            break;
//    }
//}


    

@end

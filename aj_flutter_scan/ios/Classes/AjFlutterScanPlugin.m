#import "AjFlutterScanPlugin.h"
#import "BarcodeScannerViewController.h"

@implementation AjFlutterScanPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"aj_flutter_scan"
                                     binaryMessenger:[registrar messenger]];
    AjFlutterScanPlugin* instance = [[AjFlutterScanPlugin alloc] init];
    instance.hostViewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getBarCode" isEqualToString:call.method]) {
        self.result = result;
        [self showBarcodeView:call.arguments[@"scan_title"]];
    } else if ([@"checkQRCode" isEqualToString:call.method]) {
        self.result = result;
        NSString *documentDirectory = [NSString stringWithFormat:@"%@",call.arguments[@"imageFile"]];
        UIImage *image = [UIImage imageWithContentsOfFile:documentDirectory];
        [self checkQR:image];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)showBarcodeView:(NSString *)scanTitle {
    BarcodeScannerViewController *scannerViewController = [[BarcodeScannerViewController alloc] initWithOptions:@{@"scan_title" : scanTitle}];
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:scannerViewController];
    scannerViewController.delegate = self;
    navigationController.modalPresentationStyle = UIModalPresentationFullScreen;
    [self.hostViewController presentViewController:navigationController animated:NO completion:nil];
}

- (void)barcodeScannerViewController:(BarcodeScannerViewController *)controller didScanBarcodeWithResult:(NSString *)result {
    if (self.result) {
        self.result(result);
    }
}

- (void)barcodeScannerViewController:(BarcodeScannerViewController *)controller didFailWithErrorCode:(NSString *)errorCode {
    if (self.result){
        self.result([FlutterError errorWithCode:errorCode
                                        message:nil
                                        details:nil]);
    }
}

- (void)checkQR:(UIImage *)image{
//    ZBarReaderController *reader = [[ZBarReaderController alloc] init];
//    CGImageRef cgimage = image.CGImage;
//    ZBarSymbol *symbol = nil;
//    for(symbol in [reader scanImage:cgimage])
//        break;
//    NSString *urlStr = symbol.data;
//    if (urlStr==nil || urlStr.length<=0) {
//        self.result([FlutterError errorWithCode:@"CHECK_ERROR"
//                                        message:nil
//                                        details:nil]);
//    } else{
//        self.result(urlStr);
//    }
    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:nil options:@{ CIDetectorAccuracy : CIDetectorAccuracyHigh }];// 二维码识别
    NSArray *features = [detector featuresInImage:[CIImage imageWithCGImage:image.CGImage]];
    if (features.count >= 1) {
        CIQRCodeFeature *feature = [features objectAtIndex:0];
        NSString *scannedResult = feature.messageString;
        self.result(scannedResult);
    } else{
        self.result([FlutterError errorWithCode:@"CHECK_ERROR"
                                        message:nil
                                        details:nil]);
    }
}
@end

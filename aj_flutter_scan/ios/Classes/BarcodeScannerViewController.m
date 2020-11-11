#import "BarcodeScannerViewController.h"
#import <MTBBarcodeScanner/MTBBarcodeScanner.h>
#import "ScannerOverlay.h"
#import "QRHelper.h"
//#import "ZBarSDK.h"



@implementation BarcodeScannerViewController {
    UIImagePickerController *_picker;//系统相册视图
    NSString *_scanTitle;
}

- (id)initWithOptions:(NSDictionary *)options{
    self = [super init];
    if (self) {
        _scanTitle = options[@"scan_title"];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = UIColor.clearColor;
    self.previewView = [[UIView alloc] initWithFrame:self.view.bounds];
    self.previewView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:_previewView];
    [self.view addConstraints:[NSLayoutConstraint
            constraintsWithVisualFormat:@"V:[previewView]"
                                options:NSLayoutFormatAlignAllBottom
                                metrics:nil
                                  views:@{@"previewView": _previewView}]];
    [self.view addConstraints:[NSLayoutConstraint
            constraintsWithVisualFormat:@"H:[previewView]"
                                options:NSLayoutFormatAlignAllBottom
                                metrics:nil
                                  views:@{@"previewView": _previewView}]];
  self.scanRect = [[ScannerOverlay alloc] initWithFrame:self.view.bounds];
  self.scanRect.translatesAutoresizingMaskIntoConstraints = NO;
  self.scanRect.backgroundColor = UIColor.clearColor;
  [self.view addSubview:_scanRect];
  [self.view addConstraints:[NSLayoutConstraint
                             constraintsWithVisualFormat:@"V:[scanRect]"
                             options:NSLayoutFormatAlignAllBottom
                             metrics:nil
                             views:@{@"scanRect": _scanRect}]];
  [self.view addConstraints:[NSLayoutConstraint
                             constraintsWithVisualFormat:@"H:[scanRect]"
                             options:NSLayoutFormatAlignAllBottom
                             metrics:nil
                             views:@{@"scanRect": _scanRect}]];
  [_scanRect startAnimating];
    self.scanner = [[MTBBarcodeScanner alloc] initWithPreviewView:_previewView];
    [self setNav];
  [self updateFlashButton];
}


- (void)setNav {
    BOOL isIPhone8X = [UIScreen mainScreen].bounds.size.height >= 812 ? YES : NO;
    CGFloat kStatusBarH = isIPhone8X ? 44.0 : 20.0;
    
    self.backLabelButton = [[BackButton alloc]init];
    self.backLabelButton.frame = CGRectMake(10, [UIScreen mainScreen].bounds.size.height - 80, 40, 40);
    self.backLabelButton.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.6];
    self.backLabelButton.layer.cornerRadius = 20;
    self.backLabelButton.layer.masksToBounds = true;
//    [self.backLabelButton setImage:[UIImage imageNamed:@"navigaton-back-button-bg"] forState:UIControlStateNormal];
    [self.backLabelButton addTarget:self action:@selector(cancel) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.backLabelButton];
    
    self.flashButton = [[FlashButton alloc]init];
    //130 174
    self.flashButton.frame = CGRectMake([UIScreen mainScreen].bounds.size.width - 60, kStatusBarH + 5, 30, 30);
    [self.flashButton setNeedsDrawColor:UIColor.whiteColor];
//    self.flashButton.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.6];

//    [self.flashButton setImage:[UIImage imageNamed:@"flash_off"] forState:UIControlStateNormal];
//    [self.flashButton setImage:[UIImage imageWithContentsOfFile:[self getBundleImage:@"flash_off"]] forState:UIControlStateNormal];
    [self.flashButton addTarget:self action:@selector(toggle) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.flashButton];
    
    CGRect rect = self.view.frame;
    CGFloat heightMultiplier = 3.0/4.0; // 4:3 aspect ratio
    CGFloat scanRectWidth = rect.size.width * 0.8f;
    CGFloat scanRectHeight = scanRectWidth * heightMultiplier;
    CGFloat scanRectOriginY = (rect.size.height / 2) - (scanRectHeight / 2);
    UILabel *titleLabel = [[UILabel alloc]init];
    titleLabel.frame = CGRectMake(0, scanRectOriginY - 50, [UIScreen mainScreen].bounds.size.width, 40);
    titleLabel.text = _scanTitle;
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.font = [UIFont systemFontOfSize:13];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:titleLabel];
    
    
//    UIButton *photoButton = [[UIButton alloc]init];
//    photoButton.frame = CGRectMake([UIScreen mainScreen].bounds.size.width - 120, kStatusBarH + 5, 30, 30);
//    [photoButton setImage:[UIImage imageNamed:@"photo"] forState:UIControlStateNormal];
//    [photoButton addTarget:self action:@selector(photoButtonClick) forControlEvents:UIControlEventTouchUpInside];
//    [self.view addSubview:photoButton];
}

- (NSString *)getBundleImage:(NSString *)imageName {
    NSString * bundlePath = [[ NSBundle mainBundle] pathForResource: @ "scan_resources"ofType :@ "bundle"];

    NSBundle *resourceBundle = [NSBundle bundleWithPath:bundlePath];
    NSString *img_path = [resourceBundle pathForResource:imageName ofType:@"png"];
    return img_path;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (self.scanner.isScanning) {
        [self.scanner stopScanning];
    }
    [MTBBarcodeScanner requestCameraPermissionWithSuccess:^(BOOL success) {
        if (success) {
            [self startScan];
        } else {
          [self.delegate barcodeScannerViewController:self didFailWithErrorCode:@"PERMISSION_NOT_GRANTED"];
          [self dismissViewControllerAnimated:NO completion:nil];
        }
    }];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.hidden = YES;
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.scanner stopScanning];
    [super viewWillDisappear:animated];
    if ([self isFlashOn]) {
        [self toggleFlash:NO];
    }
}

- (void)startScan {
    NSError *error;
    [self.scanner startScanningWithResultBlock:^(NSArray<AVMetadataMachineReadableCodeObject *> *codes) {
//        [QRHelper playAudioWithSoundName:@"noticeMusic.caf"];
        [self.scanner stopScanning];
         AVMetadataMachineReadableCodeObject *code = codes.firstObject;
        if (code) {
            [self.delegate barcodeScannerViewController:self didScanBarcodeWithResult:code.stringValue];
            [self dismissViewControllerAnimated:NO completion:nil];
        }
    } error:&error];
}

- (void)cancel {
    [self.delegate barcodeScannerViewController:self didFailWithErrorCode:@"SCAN_CANCLE"];
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (void)updateFlashButton {
    if (!self.hasTorch) {
        return;
    }
    if (self.isFlashOn) {
//        self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Flash Off"
//                                                                                  style:UIBarButtonItemStylePlain
//                                                                                 target:self action:@selector(toggle)];
//        [self.flashButton setImage:[UIImage imageNamed:@"flash_on"] forState:UIControlStateNormal];
//        [self.flashButton setImage:[UIImage imageWithContentsOfFile:[self getBundleImage:@"flash_on"]] forState:UIControlStateNormal];
        [self.flashButton setNeedsDrawColor:[UIColor colorWithRed:95/255.0 green:144/255.0 blue:232/255.0 alpha:1/1.0]];

    } else {
//        [self.flashButton setImage:[UIImage imageWithContentsOfFile:[self getBundleImage:@"flash_off"]] forState:UIControlStateNormal];
//        [self.flashButton setImage:[UIImage imageNamed:@"flash_off"] forState:UIControlStateNormal];
        [self.flashButton setNeedsDrawColor:UIColor.whiteColor];

    }
}

- (void)toggle {
    [self toggleFlash:!self.isFlashOn];
    [self updateFlashButton];
}

- (BOOL)isFlashOn {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if (device) {
        return device.torchMode == AVCaptureFlashModeOn || device.torchMode == AVCaptureTorchModeOn;
    }
    return NO;
}

- (BOOL)hasTorch {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if (device) {
        return device.hasTorch;
    }
    return false;
}

- (void)toggleFlash:(BOOL)on {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if (!device) return;

    NSError *err;
    if (device.hasFlash && device.hasTorch) {
        [device lockForConfiguration:&err];
        if (err != nil) return;
        if (on) {
            device.flashMode = AVCaptureFlashModeOn;
            device.torchMode = AVCaptureTorchModeOn;
        } else {
            device.flashMode = AVCaptureFlashModeOff;
            device.torchMode = AVCaptureTorchModeOff;
        }
        [device unlockForConfiguration];
    }
}

- (void)photoButtonClick {
    //停止扫描
    if (self.scanner.isScanning) {
        [self.scanner stopScanning];
    }
    _picker = [[UIImagePickerController alloc] init];
    _picker.delegate = self;
    _picker.allowsEditing = NO;
    _picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    _picker.view.frame = self.view.frame;
    [window addSubview:_picker.view];
}

#pragma mark -- UIImagePickerControllerDelegate
-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    //收起相册
    [picker.view removeFromSuperview];
    //重新扫描
    [MTBBarcodeScanner requestCameraPermissionWithSuccess:^(BOOL success) {
        if (success) {
            [self startScan];
        } else {
            [self.delegate barcodeScannerViewController:self didFailWithErrorCode:@"PERMISSION_NOT_GRANTED"];
            [self dismissViewControllerAnimated:NO completion:nil];
        }
    }];
    
}

//导入二维码的时候会进入此方法，处理选中的相片获取二维码内容
-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    
    //处理选中的相片,获得二维码里面的内容
    UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
    if (!image){
        image = [info objectForKey:UIImagePickerControllerOriginalImage];
    }
    [picker.view removeFromSuperview];
    [self checkQR:image];
    

}


- (void)checkQR:(UIImage *)image{
//    ZBarReaderController *reader = [[ZBarReaderController alloc] init];
//    CGImageRef cgimage = image.CGImage;
//    ZBarSymbol *symbol = nil;
//    for(symbol in [reader scanImage:cgimage])
//        break;
//    NSString *urlStr = symbol.data;
//    if (urlStr==nil || urlStr.length<=0) {
//        //二维码内容解析失败
//        UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"扫描失败" message:nil preferredStyle:UIAlertControllerStyleAlert];
//        __weak __typeof(self) weakSelf = self;
//        UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
//            //重新扫描
//            [MTBBarcodeScanner requestCameraPermissionWithSuccess:^(BOOL success) {
//                if (success) {
//                    [weakSelf startScan];
//                } else {
//                    [weakSelf.delegate barcodeScannerViewController:self didFailWithErrorCode:@"PERMISSION_NOT_GRANTED"];
//                    [weakSelf dismissViewControllerAnimated:NO completion:nil];
//                }
//            }];
//
//        }];
//        [alertVC addAction:action];
//        [self presentViewController:alertVC animated:YES completion:^{
//        }];
//        return;
//
//    } else{
//        if (urlStr) {
//            [self.delegate barcodeScannerViewController:self didScanBarcodeWithResult:urlStr];
//            [self dismissViewControllerAnimated:NO completion:nil];
//        }
//    }
    
    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:nil options:@{ CIDetectorAccuracy : CIDetectorAccuracyHigh }];// 二维码识别
    NSArray *features = [detector featuresInImage:[CIImage imageWithCGImage:image.CGImage]];
    if (features.count >= 1) {
        CIQRCodeFeature *feature = [features objectAtIndex:0];
        NSString *scannedResult = feature.messageString;
        NSLog(@"---- %@", scannedResult);
        if (scannedResult) {
            [self.delegate barcodeScannerViewController:self didScanBarcodeWithResult:scannedResult];
            [self dismissViewControllerAnimated:NO completion:nil];
        }
    } else{
        //二维码内容解析失败
        UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"扫描失败" message:nil preferredStyle:UIAlertControllerStyleAlert];
        __weak __typeof(self) weakSelf = self;
        UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
            //重新扫描
            [MTBBarcodeScanner requestCameraPermissionWithSuccess:^(BOOL success) {
                if (success) {
                    [weakSelf startScan];
                } else {
                    [weakSelf.delegate barcodeScannerViewController:self didFailWithErrorCode:@"PERMISSION_NOT_GRANTED"];
                    [weakSelf dismissViewControllerAnimated:NO completion:nil];
                }
            }];

        }];
        [alertVC addAction:action];
        [self presentViewController:alertVC animated:YES completion:^{
        }];
        return;

    }
}

@end

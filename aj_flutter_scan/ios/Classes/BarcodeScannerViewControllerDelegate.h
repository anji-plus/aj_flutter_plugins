#import <Foundation/Foundation.h>

@class BarcodeScannerViewController;

@protocol BarcodeScannerViewControllerDelegate <NSObject>

- (void)barcodeScannerViewController:(BarcodeScannerViewController *)controller didScanBarcodeWithResult:(NSString *)result;
- (void)barcodeScannerViewController:(BarcodeScannerViewController *)controller didFailWithErrorCode:(NSString *)errorCode;

@end

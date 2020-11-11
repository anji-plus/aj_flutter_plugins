#import <Foundation/Foundation.h>
#import <MTBBarcodeScanner/MTBBarcodeScanner.h>

#import "BarcodeScannerViewControllerDelegate.h"
#import "ScannerOverlay.h"
#import "BackButton.h"
#import "FlashButton.h"
//#import "ZBarSDK.h"

@interface BarcodeScannerViewController : UIViewController<UIImagePickerControllerDelegate,UINavigationControllerDelegate>
@property(nonatomic, retain) UIView *previewView;
  @property(nonatomic, retain) ScannerOverlay *scanRect;
@property(nonatomic, retain) MTBBarcodeScanner *scanner;
@property(nonatomic, weak) id<BarcodeScannerViewControllerDelegate> delegate;

@property(nonatomic, retain) BackButton *backLabelButton;
@property(nonatomic, retain) FlashButton *flashButton;


  -(id) initWithOptions:(NSDictionary *) options;
@end

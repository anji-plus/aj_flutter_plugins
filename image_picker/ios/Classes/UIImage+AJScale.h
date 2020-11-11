//
//  UIImage+AJScale.h
//  image_picker
//
//  Created by Black on 2019/9/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (AJScale)
//图片调整方向
- (UIImage *)fixOrientation;
//偏转
- (UIImage *)rotate:(CGFloat)rotate;
- (UIImage *)scaleWithMinWidth:(CGFloat)minWidth minHeight:(CGFloat)minHeight;
@end

NS_ASSUME_NONNULL_END

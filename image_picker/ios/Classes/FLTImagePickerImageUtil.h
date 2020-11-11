// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface GIFInfo : NSObject

@property(strong, nonatomic, readonly) NSArray<UIImage *> *images;
@property(assign, nonatomic, readonly) NSTimeInterval interval;

- (instancetype)initWithImages:(NSArray<UIImage *> *)images interval:(NSTimeInterval)interval;

@end

@interface FLTImagePickerImageUtil : NSObject

+ (UIImage *)scaledImage:(UIImage *)image
                maxWidth:(NSNumber *)maxWidth
               maxHeight:(NSNumber *)maxHeight;

// Resize all gif animation frames.
+ (GIFInfo *)scaledGIFImage:(NSData *)data
                   maxWidth:(NSNumber *)maxWidth
                  maxHeight:(NSNumber *)maxHeight;

//压缩图片指定大小 单位kb; 如果外部传入尺寸不需要调整大小
+ (UIImage *)compressImage:(UIImage *)image
         maxDataSizeKBytes:(double)maxSize
            needChangeSize: (BOOL)needChange;

@end

NS_ASSUME_NONNULL_END

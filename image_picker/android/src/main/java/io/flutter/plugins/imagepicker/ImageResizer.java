// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.imagepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class ImageResizer {
    private final File externalFilesDirectory;
    private final ExifDataCopier exifDataCopier;

    ImageResizer(File externalFilesDirectory, ExifDataCopier exifDataCopier) {
        this.externalFilesDirectory = externalFilesDirectory;
        this.exifDataCopier = exifDataCopier;
    }

    //采样率处理，避免decode出现OOM
    private Bitmap getZoomBitmapFromURI(String path, int targetWidth, int targetHeight) {
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, ops);
        ops.inSampleSize = 1;
        int oHeight = ops.outHeight;
        int oWidth = ops.outWidth;
        int angle = PhotoBitmapUtils.readPictureDegree(path);

        //把角度转回来再算
        if (angle == 90 || angle == 270) {
            int temp = oHeight;
            oHeight = oWidth;
            oWidth = temp;
        }
        //合理降低采样率，原来向上取整，如今向下，影响不大
        if (((float) oHeight / targetHeight) < ((float) oWidth / targetWidth)) {
            ops.inSampleSize = (int) Math.floor((float) oWidth / targetWidth);
        } else {
            ops.inSampleSize = (int) Math.floor((float) oHeight / targetHeight);
        }
        if (ops.inSampleSize <= 0) {
            ops.inSampleSize = 1;
        }
        ops.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, ops);
    }

    /**
     * @param context
     * @param path
     * @param maxWidth
     * @param maxHeight
     * @return 裁剪后的bitmap
     */
    public Bitmap resizedBitmap(Context context, String path, Double maxWidth, Double maxHeight) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        if (maxWidth == null || maxHeight == null) {
            maxWidth = screenWidth * 1.0;
            maxHeight = screenHeight * 1.0;
        }
        //保持比例
        if (maxWidth != null && maxWidth > screenWidth) {
            maxHeight = (maxWidth / screenWidth) * screenHeight;
            maxWidth = screenWidth * 1.;
        }
        //采样率处理
        return getZoomBitmapFromURI(path, maxWidth.intValue(), maxHeight.intValue());
    }
}

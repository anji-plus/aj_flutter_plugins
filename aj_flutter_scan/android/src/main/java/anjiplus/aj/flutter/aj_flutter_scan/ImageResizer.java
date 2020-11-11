// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package anjiplus.aj.flutter.aj_flutter_scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageResizer {
    private File externalFilesDirectory;
    private ExifDataCopier exifDataCopier;

    ImageResizer(File externalFilesDirectory, ExifDataCopier exifDataCopier) {
        this.externalFilesDirectory = externalFilesDirectory;
        this.exifDataCopier = exifDataCopier;
    }

    /**
     * If necessary, resizes the image located in imagePath and then returns the path for the scaled
     * image.
     *
     * <p>If no resizing is needed, returns the path for the original image.
     */
    String resizeImageIfNeeded(Context context, String imagePath, Double maxWidth, Double maxHeight) {
        boolean shouldScale = maxWidth != null || maxHeight != null;

        if (!shouldScale) {
            return imagePath;
        }

        try {
            File scaledImage = resizedImage(context, imagePath, maxWidth, maxHeight);
            exifDataCopier.copyExif(imagePath, scaledImage.getPath());

            return scaledImage.getPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //避免OOM
    private Bitmap getZoomBitmapFromURI(String path, int targetWidth, int targetHeight) {
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeFile(path, ops);
        ops.inSampleSize = 1;
        int oHeight = ops.outHeight;
        int oWidth = ops.outWidth;

        if (((float) oHeight / targetHeight) < ((float) oWidth / targetWidth)) {
            ops.inSampleSize = (int) Math.ceil((float) oWidth / targetWidth);
        } else {
            ops.inSampleSize = (int) Math.ceil((float) oHeight / targetHeight);
        }
        ops.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, ops);
        return bm;
    }

    private File resizedImage(Context context, String path, Double maxWidth, Double maxHeight) throws IOException {
        //可能内存溢出，优化step1
//        Bitmap bmp = BitmapFactory.decodeFile(path);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        if (maxWidth == null || maxHeight == null) {
            maxWidth = screenWidth * 1.0;
            maxHeight = screenHeight * 1.0;
        }
        //保持比例
        if (maxWidth != null && maxWidth > screenWidth) {
            maxHeight = (maxWidth / screenWidth) * screenHeight;
            maxWidth = screenWidth * 1.0;
        }
        //清晰度处理
        Bitmap bmp = getZoomBitmapFromURI(path, maxWidth.intValue(), maxHeight.intValue());

        //压缩保存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean saveAsPNG = bmp.hasAlpha();
        bmp.compress(
                saveAsPNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, outputStream);

        String[] pathParts = path.split("/");
        String imageName = pathParts[pathParts.length - 1];

        File imageFile = new File(externalFilesDirectory, "/scaled_" + imageName);
        FileOutputStream fileOutput = new FileOutputStream(imageFile);
        fileOutput.write(outputStream.toByteArray());
        fileOutput.close();

        return imageFile;
    }
}

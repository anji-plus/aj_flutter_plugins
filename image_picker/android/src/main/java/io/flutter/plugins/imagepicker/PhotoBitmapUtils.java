package io.flutter.plugins.imagepicker;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SummerChen on 2019/7/25.
 * mod by chenhailong on 2020/3/14.
 */
public class PhotoBitmapUtils {

    //获取旋转后图片保存后的路径
    static String getRotateSavedFilePath(File externalFilesDirectory, Bitmap bmp, String originPath) {
        //旋转处理
        bmp = getRotateBitmap(originPath, bmp);
        //保存获得路径
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean saveAsPNG = bmp.hasAlpha();
        bmp.compress(
                saveAsPNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, outputStream);
        String[] pathParts = originPath.split("/");
        String imageName = pathParts[pathParts.length - 1];
        File imageFile = new File(externalFilesDirectory, "/scaled_" + imageName);
        try {
            FileOutputStream fileOutput = new FileOutputStream(imageFile);
            fileOutput.write(outputStream.toByteArray());
            fileOutput.close();
        } catch (Exception e) {

        }
        return imageFile.getAbsolutePath();
    }

    //旋转处理
    static Bitmap getRotateBitmap(String originPath, Bitmap bmp) {
        int angle = readPictureDegree(originPath);
        return rotateBitmap(angle, bmp);
    }

    /**
     * 裁剪后压缩图片
     *
     * @param bmp            裁剪后图片
     * @param compressSizeKb 压缩的最大尺寸 kb
     * @param externalFilesDirectory 外部存储
     * @param originPath 原始图片存储路径
     * @return 最终要返回的图片路径
     */
    public static String getCompressPhotoUrl(File externalFilesDirectory, Bitmap bmp, String originPath, Double compressSizeKb) {
        if (null == compressSizeKb || compressSizeKb <= 0) {
            return getRotateSavedFilePath(externalFilesDirectory, bmp, originPath);
        }
        int oldSize = 0;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

            oldSize = baos.toByteArray().length / 1024;
            Log.i("123", "测试压缩前的图片大小ddd：" + oldSize + "kB" + "压缩要求：" + compressSizeKb + "时间：" + System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    //关闭
                    baos.close();
                }
            } catch (IOException e) {

            }
        }

        //比上限kb小，直接处理旋转后保存
        if (oldSize < compressSizeKb.intValue()) {
            return getRotateSavedFilePath(externalFilesDirectory, bmp, originPath);
        }

        String finalImagePath = "";

        try {
            //创建临时文件 TODO
//            File temp = File.createTempFile("img", String.format("%d", System.currentTimeMillis()) + ".jpg");
            //压缩图片
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
//            //100表示不进行压缩，70表示压缩率为30%
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            //写入
//            bos.flush();
//            //关闭
//            bos.close();
            //旋转后压缩，最好压缩后旋转，但是考虑到已经对采样率做处理，对性能影响很小，所以可忽略
            bmp = getRotateBitmap(originPath, bmp);
            //压缩到多少kb以下 大小已经锁定 只能压缩质量
            finalImagePath = compressLargeImage(bmp, compressSizeKb.intValue());
        } catch (Exception e) {

        } finally {
            //回收bitmap
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
        }

        return finalImagePath;
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * @param image
     * @param kbSize 若未设置，默认压缩限制为1M
     * @return
     */
    public static String compressLargeImage(Bitmap image, int kbSize) {
        int kb = kbSize;
        if (kbSize <= 0) {
            kb = 1024;
        }
        int options = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        while (baos.toByteArray().length / 1024 > kb) {  //循环判断如果压缩后图片是否大于1M,大于继续压缩
            Log.i("123", " comprss" + baos.toByteArray().length);
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            if (options == 0) {
                break;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        //保存
        File file = null;
        try {
            file = File.createTempFile("img", String.format("%d", System.currentTimeMillis()) + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            Log.i("123", "测试裁压缩后的图片大小：" + getFileSize(file) / 1024 + "kB" + "时间：" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

}

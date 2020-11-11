package anjiplus.aj.flutter.aj_flutter_scan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SummerChen on 2019/7/25.
 */
public class PhotoBitmapUtils {
    /**
     * 存放拍摄图片的文件夹
     */
    private static final String FILES_NAME = "/MyPhoto";
    /**
     * 获取的时间格式
     */
    public static final String TIME_STYLE = "yyyyMMddHHmmss";
    /**
     * 图片种类
     */
    public static final String IMAGE_TYPE = ".png";
    public static final String SAVE_PATH =
            Environment.getExternalStorageDirectory() + "/crm/";

    // 防止实例化
    private PhotoBitmapUtils() {
    }

    /**
     * 获取手机可存储路径
     *
     * @param context 上下文
     * @return 手机可存储路径
     */
    private static String getPhoneRootPath(Context context) {
        // 是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            // 获取SD卡根目录
            return context.getExternalCacheDir().getPath();
        } else {
            // 获取apk包下的缓存路径
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 使用当前系统时间作为上传图片的名称
     *
     * @return 存储的根路径+图片名称
     */
    public static String getPhotoFileName(Context context) {
        File file = new File(getPhoneRootPath(context) + FILES_NAME);
        // 判断文件是否已经存在，不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 设置图片文件名称
        SimpleDateFormat format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        String photoName = "/" + time + IMAGE_TYPE;
        return file + photoName;
    }

    /**
     * 保存Bitmap图片在SD卡中
     * 如果没有SD卡则存在手机中
     *
     * @param mbitmap 需要保存的Bitmap图片
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    public static String savePhotoToSD(Bitmap mbitmap, Context context) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(context);
        try {
            outStream = new FileOutputStream(fileName);
            // 把数据写入文件，100表示不压缩
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // 记得要关闭流！
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {

        File file = new File(path);
        Bitmap bmp = null;
        try {
            int oldSize = (int) (getFileSize(file) / 1024);
            //如果小于1M不压缩
            if (oldSize < 1024) {
                bmp = BitmapFactory.decodeFile(path);
                return bmp;
            } else {
                //压缩大小 快
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 5;  // 图片的大小设置为原来的五分之一
                bmp = BitmapFactory.decodeFile(path, options);
                options = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
        //压缩质量 慢
//        Bitmap image = BitmapFactory.decodeFile(path);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
//        //不压缩 直接旋转 更慢
//        Bitmap bmp = BitmapFactory.decodeFile(path);
//        return bitmap;
    }

    /**
     * 裁剪后压缩图片
     *
     * @param screenImagePath 裁剪后图片的大小
     * @param compressSizeKb  压缩的最大尺寸 kb
     * @return
     */
    public static String getCompressPhotoUrl(String screenImagePath, Double compressSizeKb) {
        //第一步 图片旋转
        //第二部 先调整下像素 防止OOM
        //前两步已经写好了在前面
        //第三部 质量循环压缩到指定大小
        if (null == compressSizeKb) {
            return screenImagePath;
        }
        File file = new File(screenImagePath);

        String finalImagePath = screenImagePath;
        try {
            int oldSize = (int) (getFileSize(file) / 1024);
            Log.i("123", "测试压缩前的图片大小ddd：" + oldSize + "kB" + "压缩要求：" + compressSizeKb + "时间：" + System.currentTimeMillis());
            if (oldSize < compressSizeKb.intValue()) {
                return screenImagePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(screenImagePath);

        //文件保存
        try {
            //创建临时文件
            File temp = File.createTempFile("img", String.format("%d", System.currentTimeMillis()) + ".jpg");
            //压缩图片
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
            //100表示不进行压缩，70表示压缩率为30%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            //写入
            bos.flush();
            //关闭
            bos.close();
            //压缩到多少kb以下 大小已经锁定 只能压缩质量
            finalImagePath = compressLargeImage(bitmap, compressSizeKb.intValue(), temp);
        } catch (Exception e) {

        } finally {
            //回收bitmap
            bitmap.recycle();
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
     * @param kbSize 考虑到压缩到150K，第一次压缩限制1M内
     * @return
     */
    public static String compressLargeImage(Bitmap image, int kbSize, File file) {
        int kb = kbSize;
        if (kbSize <= 0) {
            kb = 1024;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中


        while (baos.toByteArray().length / 1024 > kb) {  //循环判断如果压缩后图片是否大于1M,大于继续压缩
            Log.i("123", " comprss" + baos.toByteArray().length);
            //先压缩几倍
//            int scaleSize = baos.toByteArray().length / 1024 / kb;
//            if (scaleSize > 1) {
//                options = 100 / scaleSize;
//                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
//            }
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少5
            if (options == 0) {
                break;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            Log.i("123", "测试裁压缩后的图片大小：" + getFileSize(file) / 1024 + "kB" + "时间：" + System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 处理旋转后的图片
     *
     * @param originpath 原图路径
     * @param context    上下文
     * @return 返回修复完毕后的图片路径
     */
    public static String amendRotatePhoto(String originpath, Context context) {

        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);
        //如果图片过大 先等比例缩放到1/5再压缩 防止OOM
//        Bitmap bmp=BitmapFactory.decodeFile(originpath);
        Bitmap bmp = getCompressPhoto(originpath);
        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);
        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, context);
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
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
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

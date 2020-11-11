package com.zbar.lib.decode;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.yanzhenjie.zbar.Image;
import com.yanzhenjie.zbar.ImageScanner;
import com.yanzhenjie.zbar.Symbol;
import com.yanzhenjie.zbar.SymbolSet;
import com.zbar.lib.CaptureActivity;
import com.zbar.lib.ZbarManager;
import com.zbar.lib.bitmap.PlanarYUVLuminanceSource;

import java.io.File;
import java.io.FileOutputStream;

import static com.zbar.lib.CommonMessage.msgDecode;
import static com.zbar.lib.CommonMessage.msgDecodeFailed;
import static com.zbar.lib.CommonMessage.msgDecodeSucceeded;
import static com.zbar.lib.CommonMessage.msgQuit;


final class DecodeHandler extends Handler {

    CaptureActivity activity = null;

    DecodeHandler(CaptureActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case msgDecode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case msgQuit:
                Looper.myLooper().quit();
                break;
        }
    }

    private void decode(byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;// Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

//        ZbarManager manager = new ZbarManager();
        String result = null;
//		String result = manager.decode(rotatedData, width, height, true,
//				activity.getX(), activity.getY(), activity.getCropWidth(),
//				activity.getCropHeight());
        Image barcode = new Image(width, height, "Y800");
        barcode.setCrop(activity.getX(), activity.getY(), activity.getCropWidth(), activity.getCropHeight());
        barcode.setData(rotatedData);

        ImageScanner mImageScanner = new ImageScanner();
        int code = mImageScanner.scanImage(barcode);
        if (code != 0) {
            SymbolSet symSet = mImageScanner.getResults();
            for (Symbol sym : symSet)
                result = sym.getData();
        }
        if (result != null) {
            mImageScanner.destroy();
            if (activity.isNeedCapture()) {
                // 生成bitmap
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                        rotatedData, width, height, activity.getX(),
                        activity.getY(), activity.getCropWidth(),
                        activity.getCropHeight(), false);
                int[] pixels = source.renderThumbnail();
                int w = source.getThumbnailWidth();
                int h = source.getThumbnailHeight();
                Bitmap bitmap = Bitmap.createBitmap(pixels, 0, w, w, h,
                        Bitmap.Config.ARGB_8888);
                try {
                    String rootPath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Qrcode/";
                    File root = new File(rootPath);
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File f = new File(rootPath + "Qrcode.jpg");
                    if (f.exists()) {
                        f.delete();
                    }
                    f.createNewFile();

                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != activity.getHandler()) {
                Message msg = new Message();
                msg.obj = result;
                msg.what = msgDecodeSucceeded;
                activity.getHandler().sendMessage(msg);
            }
        } else {
            if (null != activity.getHandler()) {
                activity.getHandler().sendEmptyMessage(msgDecodeFailed);
            }
        }
    }

}

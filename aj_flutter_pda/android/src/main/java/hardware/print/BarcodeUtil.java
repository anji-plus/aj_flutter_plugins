package hardware.print;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

import static android.content.ContentValues.TAG;

public final class BarcodeUtil {
    private static final int BLACK = 0xff000000;
    private static int PADDING_SIZE_MIN = 0;

    public static Bitmap create2dBarcode(String str, BarcodeFormat barcodeFormat, int deswidth, int desheight)
            throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix matrix = multiFormatWriter.encode(str,
                barcodeFormat, deswidth, desheight, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Log.d(TAG, "create2dBarcode width:" + width + "   height:" + height);
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    if (isFirstBlackPoint == false) {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                    }
                    pixels[y * width + x] = BLACK;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return removeBlankFromBitmap(bitmap, startX, startY, width, height, deswidth, desheight);
    }

    private static Bitmap removeBlankFromBitmap(Bitmap bitmap, int startX, int startY, int width, int height, int deswidth, int desheight) {

        if (true) {
            // cat 2d barcode area
            if (startX <= PADDING_SIZE_MIN) return bitmap;

            int x1 = startX - PADDING_SIZE_MIN;
            int y1 = startY - PADDING_SIZE_MIN;
            if (x1 < 0 || y1 < 0) return bitmap;

            int w1 = width - x1 * 2;
            int h1 = height - y1 * 2;

            bitmap = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
            bitmap = Bitmap.createScaledBitmap(bitmap, deswidth, desheight, true);
        }

        return bitmap;
    }

    public static Bitmap create1dBarcode(String contents, BarcodeFormat barcodeFormat, int width, int height) throws WriterException {
        return encodeAsBitmap(contents, barcodeFormat, width, height);
    }

    public static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); ++i) {
            if (contents.charAt(i) > 0xFF) {
                return "GBK";
            }
        }
        return null;
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
                                        int desiredWidth, int desiredHeight) throws WriterException {

        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {

            result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = result.getWidth();
        int height = result.getHeight();
        Log.d(TAG, "encodeAsBitmap width:" + width + "   height:" + height);
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;

        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                if (isFirstBlackPoint == false && result.get(x, y)) {
                    isFirstBlackPoint = true;
                    startX = x;
                    startY = y;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return removeBlankFromBitmap(bitmap, startX, startY, width, height, desiredWidth, desiredHeight);
    }

    public static Bitmap creatBarcode(Context context,
                                      BarcodeFormat barcodeFormat, String contents, int desiredWidth,
                                      int desiredHeight, boolean displayCode) throws WriterException {
        Bitmap ruseltBitmap = null;
        /**
         * Image left and right blank
         */
        int marginW = 5;
        if (displayCode) {
            // Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
            // desiredWidth, desiredHeight);
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);

            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth, 5, context);
            //Bitmap codeBitmap = creatCodeBitmap(contents,desiredWidth+2*marginW, desiredHeight, context);

            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
        } else {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
        }
        return ruseltBitmap;
    }

    /**
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap creatCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * @param first
     * @param second
     * @param fromPoint
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap
                .createBitmap(first.getWidth(),
                        first.getHeight() + marginW,
                        Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save();
        cv.restore();
        return newBitmap;
    }
}

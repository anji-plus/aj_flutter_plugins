package hardware.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/************************************************************
 * Copyright 2000-2066 Olc Corp., Ltd.
 * All rights reserved.
 * <p>
 * Description     : The Main activity for the Camera application
 * History        :( ID, Date, Author, Description)
 * v1.0, 2017/1/20,  Administrator, create
 ************************************************************/
//注意，不能更改类名
public class printer {

    static public native int Open();

    static public native int Close();

    static public native int Step(byte bStep);

    static public native int Unreeling(byte bStep);

    static public native void GoToNextPage();

    static public native int PrintImage(short[] data);

    static public native int PrintImageEx(byte[] data, int nBit);

    static public native int PrintString24(byte[] data);

    static public native int SetGrayLevel(byte blevel);

    static {
        System.loadLibrary("hardware-print");
    }

    static String[] g_str = {"00", "01", "02", "03", "04", "05", "06", "07",
            "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D",
            "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33",
            "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E",
            "3F", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53", "54",
            "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A",
            "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75",
            "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80",
            "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B",
            "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96",
            "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F", "A0", "A1",
            "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC",
            "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7",
            "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1", "C2",
            "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD",
            "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8",
            "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3",
            "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE",
            "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9",
            "FA", "FB", "FC", "FD", "FE", "FF"};

    public enum PrintType {
        Left,
        // just Horizontal center
        Centering,
        VerticalCentering,
        VerticalHorizontalCentering,
        TopCentering,
        LeftTop,
        RightTop,
        Right,
    }

    ;
    private Paint mPaint = new Paint();
    private int mMaxWidth = 384;
    private String mStrPrint = "";
    private int mLineMaxTextSize = 0;
    private Bitmap mLineBitmap = null;

    public Paint GetPaint() {
        return mPaint;
    }

    public void PrintLineInit(int lineMaxSize) {
        mPaint.setTextSize(lineMaxSize);
        mPaint.setFakeBoldText(true);
        Paint.FontMetrics font = mPaint.getFontMetrics();
        int hi = (int) Math.ceil(font.descent - font.ascent);
        mLineMaxTextSize = lineMaxSize;
        mLineBitmap = Bitmap.createBitmap(mMaxWidth, hi, Bitmap.Config.ARGB_4444);
        if (mLineBitmap != null) {
            mPaint.setColor(Color.WHITE);
            Canvas can = new Canvas(mLineBitmap);
            can.drawRect(0, 0, mMaxWidth, hi, mPaint);
        }
    }

    public void PrintLineString(String str, int textSize, int nLeft, boolean bBold) {
        if (mLineBitmap != null) {
            mPaint.setTextSize(textSize);
            mPaint.setFakeBoldText(bBold);
            float y = Math.abs(mPaint.ascent());
            PrintLineOneString(str, nLeft, y, mLineBitmap);
        }
    }

    public void PrintLineStringByType(String str, int textSize, printer.PrintType type, boolean bBold) {
        if (mLineBitmap != null) {
            float x = 0;
            float y = 0;
            mPaint.setTextSize(textSize);
            mPaint.setFakeBoldText(bBold);

            Paint.FontMetrics font = mPaint.getFontMetrics();
            int textHeight = (int) Math.ceil(font.descent - font.ascent);
            switch (type) {
                case Left:
                    x = 0;
                    y = Math.abs(mPaint.ascent());
                    break;
                case VerticalCentering:
                    x = 0;
                    y = textHeight / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
                    break;
                case VerticalHorizontalCentering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = textHeight / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
                    break;
                case Centering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = Math.abs(mPaint.ascent());
                    break;
                case Right:
                    x = mMaxWidth - mPaint.measureText(str);
                    y = Math.abs(mPaint.ascent());
                    break;
                case TopCentering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = Math.abs(mPaint.ascent());
                    //hi=hi;
                    break;
                case LeftTop:
                    x = 0;
                    y = Math.abs(mPaint.ascent());
                    break;
                case RightTop:
                    x = mMaxWidth - mPaint.measureText(str);
                    y = Math.abs(mPaint.ascent());
                    break;
            }
            PrintLineOneString(str, x, y, mLineBitmap);
        }
    }

    //自定义打印文本
    public void PrintLineStringByType(String str, int textSize, int marginLeft, int marginRight, printer.PrintType type, boolean bUnderline, boolean bBold) {
        if (mLineBitmap != null) {
            float x = 0;
            float y = 0;
            mPaint.setTextSize(textSize);
            mPaint.setFakeBoldText(bBold);
            mPaint.setUnderlineText(bUnderline);
            Paint.FontMetrics font = mPaint.getFontMetrics();
            int textHeight = (int) Math.ceil(font.descent - font.ascent);
            switch (type) {
                case Left:
                    x = marginLeft;
                    y = Math.abs(mPaint.ascent());
                    break;
                case VerticalCentering:
                    x = 0;
                    y = textHeight / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
                    break;
                case VerticalHorizontalCentering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = textHeight / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
                    break;
                case Centering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = Math.abs(mPaint.ascent());
                    break;
                case Right:
                    x = mMaxWidth - mPaint.measureText(str) - marginRight;
                    y = Math.abs(mPaint.ascent());
                    break;
                case TopCentering:
                    x = (mMaxWidth - mPaint.measureText(str)) / 2;
                    y = Math.abs(mPaint.ascent());
                    //hi=hi;
                    break;
                case LeftTop:
                    x = 0;
                    y = Math.abs(mPaint.ascent());
                    break;
                case RightTop:
                    x = mMaxWidth - mPaint.measureText(str);
                    y = Math.abs(mPaint.ascent());
                    break;
            }
            PrintLineOneString(str, x, y, mLineBitmap);
        }
    }

    void PrintLineOneString(String str, float x, float y, Bitmap bmp) {
        if (mLineBitmap != null) {
            mPaint.setColor(Color.BLACK);
            Canvas can = new Canvas(mLineBitmap);
            can.drawText(str, x, y, mPaint);
        }
    }

    public void PrintLineEnd() {
        if (mLineBitmap != null) {
            doHardwarePrint(mLineBitmap);
            mLineBitmap.recycle();
            mLineBitmap = null;
        }
    }

    public void PrintStringEx(String str, int textSize, boolean bUnderline, boolean bBold, printer.PrintType type) {
        mStrPrint = "";
        String strPrint = mStrPrint + str;

        mPaint.setTextSize(textSize);
        mPaint.setFakeBoldText(bBold);
        mPaint.setUnderlineText(bUnderline);
        Paint.FontMetrics font = mPaint.getFontMetrics();
        int textHeight = (int) Math.ceil(font.descent - font.ascent);
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, textHeight, Bitmap.Config.ARGB_4444);
        if (bmp != null) {
            Canvas can = new Canvas(bmp);
            int start = 0, end = 0;
            int[] index = new int[2];
            index[0] = 0;
            index[1] = 0;
            while (true) {
                if (GetOneLineString(mPaint, strPrint, index, textHeight)) {
                    PrintOneString(can, mPaint, strPrint.substring(index[0], index[1]), textHeight, bmp, type);
                } else
                    break;
            }
            can.setBitmap(null);
            bmp.recycle();
            bmp = null;
        }
    }

    Boolean GetOneLineString(Paint pat, String str, int[] index, int nH) {
        float width = 0;

        int maxIndex = str.length() - 1;

        if (index[1] > maxIndex) {
            return false;
        }
        if (index[1] > 0) {
            index[0] = index[1];
            if (index[0] == maxIndex) {
                index[0] = maxIndex;
                index[1] = maxIndex + 1;
                return true;
            }
        }
        int splitIndex = str.indexOf("\n", index[0]);
        if (splitIndex == index[0]) {
            //   \ntestssssss
            index[1] = index[0] + 1;
            return true;
        }


        width = pat.measureText(str.substring(index[0], index[1]));
        while ((mMaxWidth - width) > nH) {
            index[1]++;
            int spiltIndex = str.indexOf("\n", index[0]);
            if (index[1] > maxIndex) {
                //less than a line ,but has \n
                if ((spiltIndex >= index[0]) && (spiltIndex < index[1])) {
                    index[1] = spiltIndex + 1;
                    return true;
                }
                return true;
            } else if (index[1] == spiltIndex) {
                //reach a line width ,but has \n
                index[1] = spiltIndex + 1;
                return true;
            }
            width = pat.measureText(str.substring(index[0], index[1]));
        }
        while ((width - mMaxWidth) > nH) {
            index[1]--;
            if (index[1] == index[0]) {
                index[1] = index[0] + 1;
                break;
            }
            width = pat.measureText(str.substring(index[0], index[1]));
        }
        if (width > 0) {
            return true;
        } else {
            return false;
        }

    }

    Boolean GetOneString(Paint pat, String str, int[] index, int nH) {
        float width = 0;
        if (index[1] == 0) {
            width = pat.measureText(str);
            if ((width < mMaxWidth) && ((mMaxWidth - width) < nH)) {
                int nS = str.indexOf("\n", index[0]);
                //first time,short str ,and not have \n
                if ((nS >= 0) && (nS < index[1])) {
                    index[0] = index[1];
                    index[1] = nS + 1;
                    return true;
                }
                mStrPrint += str;
                return false;
            }
        }
        index[0] = index[1];
        index[1] += mMaxWidth / nH;
        int nmax = str.length() - 1;
        if (index[1] > nmax) {
            index[1] = nmax;
        }
        if (index[1] <= index[0])
            return false;
        width = pat.measureText(str.substring(index[0], index[1]));
        while ((mMaxWidth - width) > nH) {
            index[1]++;
            if (index[1] > nmax) {
                int nS = str.indexOf("\n", index[0]);
                if ((nS >= 0) && (nS < index[1])) {
                    index[1] = nS + 1;
                    return true;
                }
                mStrPrint += str.substring(index[0]);
                return false;
            }
            width = pat.measureText(str.substring(index[0], index[1]));
        }
        while ((width - mMaxWidth) > nH) {
            index[1]--;
            if (index[1] == index[0]) {
                index[1] = index[0] + 1;
                break;
                // return true;
            }
            width = pat.measureText(str.substring(index[0], index[1]));
        }
        int nS = str.indexOf("\n", index[0]);
        if ((nS >= 0) && (nS < index[1])) {
            index[1] = nS + 1;
        }
        return true;
    }

    void PrintOneString(Canvas can, Paint paint, String str, int textHeight, Bitmap bmp, printer.PrintType type) {
        paint.setColor(Color.WHITE);

        can.drawRect(new Rect(0, 0, mMaxWidth, textHeight), paint);
        float strWidth = paint.measureText(str);
        float fleft = 0;
        //zhangyong
        float y = Math.abs(paint.ascent());
        //
        switch (type) {
            case Left:
                fleft = 0;
                break;
            case VerticalHorizontalCentering:
            case Centering:
                fleft = (mMaxWidth - strWidth) / 2;
                break;
            case Right:
                fleft = mMaxWidth - strWidth;
                break;
        }
        if (str.length() > 0) {
            paint.setColor(Color.BLACK);
            can.drawText(str, fleft, y, paint);
        }
        doHardwarePrint(bmp);
    }

    //水平居中打印Bitmap
    public void PrintBitmap(final Bitmap bm) {
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);//it's must need
        canvas.drawRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), paint);
        canvas.drawBitmap(bm, (mMaxWidth - bm.getWidth()) / 2, 0, paint);

        doHardwarePrint(bmp);
    }

    //自定义打印图片
    public void PrintBitmap(final Bitmap bm, int marginLeft, int marginRight, printer.PrintType type) {
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);//it's must need
        canvas.drawRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), paint);

        switch (type) {
            case Left:
                canvas.drawBitmap(bm, marginLeft, 0, paint);
                break;
            case Centering:
                canvas.drawBitmap(bm, (mMaxWidth - bm.getWidth()) / 2, 0, paint);
                break;
            case Right:
                canvas.drawBitmap(bm, mMaxWidth - bm.getWidth() - marginRight, 0, paint);
                break;
        }


        doHardwarePrint(bmp);
    }

    public void PrintBitmapAtHorizontalCenter(final Bitmap bm) {
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);//it's must need
        canvas.drawRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), paint);
        canvas.drawBitmap(bm, (mMaxWidth - bm.getWidth()) / 2, 0, paint);

        doHardwarePrint(bmp);
    }

    public void PrintBitmap(final Bitmap bm, int x, int y) {
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);//it's must need
        canvas.drawRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), paint);
        canvas.drawBitmap(bm, x, y, paint);

        doHardwarePrint(bmp);
    }

    /*
     * Print bitmap In center of Lable Paper
     * */
    public void PrintBitmapAtCenter(Bitmap bm, int labelWidth, int labelHeight) {
        Bitmap bmp = Bitmap.createBitmap(mMaxWidth, labelHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);//it's must need
        canvas.drawRect(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), paint);
        canvas.drawBitmap(bm, (mMaxWidth - bm.getWidth()) / 2 + (mMaxWidth - labelWidth) / 2, (labelHeight - bm.getHeight()) / 2, paint);
//        Utils.stroageBitmap(bmp);
        doHardwarePrint(bmp);
    }

    public void doHardwarePrint(Bitmap bm) {
        if (bm == null) {
            return;
        }
        int nlen = bm.getHeight() * bm.getWidth();
        // bm.getConfig();
        int nBit = bm.getByteCount() / nlen;
        if (nBit == 2) {
            ShortBuffer dst = ShortBuffer.allocate(bm.getByteCount());
            bm.copyPixelsToBuffer(dst);
            dst.flip();
            short[] buf = new short[nlen];
            dst.get(buf);

            int nwrite = PrintImage(buf);
        } else {
            ByteBuffer dst = ByteBuffer.allocate(bm.getByteCount());
            bm.copyPixelsToBuffer(dst);
            dst.flip();
            byte[] buf = new byte[bm.getByteCount()];
            dst.get(buf);

            int nwrite = PrintImageEx(buf, nBit);
        }

    }

    public void printBlankLine(int height) {
        PrintLineInit(height);
        PrintStringEx("", height, false, false, printer.PrintType.Centering);
        PrintLineEnd();
    }
}

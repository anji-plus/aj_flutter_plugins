package com.anjiplus.pda;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.anjiplus.pda.readepc.DevBeep;
import com.anjiplus.pda.util.ScanHelper;
import com.anjiplus.pda.util.SysBarcodeUtil;
import com.anjiplus.pda.util.VinChangeUtil;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.olc.uhf.UhfAdapter;
import com.olc.uhf.UhfManager;
import com.olc.uhf.tech.ISO1800_6C;
import com.olc.uhf.tech.IUhfCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hardware.print.BarcodeUtil;
import hardware.print.PrintDto;
import hardware.print.printer;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StringCodec;

/**
 * PdaPlugin
 */
public class PdaPlugin implements MethodCallHandler {
    private static Registrar registrar;
    private static Result result;

    private static BasicMessageChannel sendChannel;
    //channel
    private static String pdaChannel = "com.anjiplus.pdaflutter";
    //主动推送数据channel name
    private static String pdaSendChannelName = "com.anjiplus.pdasend";
    //扫描触发的action
    private static String mBroadscanAction = "com.barcode.sendBroadcastScan";
    //监听的action
    private static String mBroadcastAction = "com.barcode.sendBroadcast";

    //扫描相关
    private static int scanmode = -1;
    private static boolean bleft = false;
    private static boolean bright = false;
    private static boolean bsound = false;

    private static Context mContext;
    private static boolean fromBtn;
    //是否识别rfid卡完毕，避免重复点击
    private static boolean recognizeComplete;
    //打印相关
    printer mPrinter = new printer();
    private String mOneBarcodeType = "128";
    private printer.PrintType mTitleType = printer.PrintType.Left;//默认居左

    //识别车架号相关
    public static UhfManager mService;
    private static int a;
    private static ISO1800_6C uhf_6c;
    private long lastTime;
    private long duration = 500;
    private byte btMemBank = 0x01;
    private static boolean isShowDialog = false;
    private String vin = "";


    //接收扫描结果
    static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastAction)) {
                String str = intent.getStringExtra("BARCODE");
                if (str != null) {
                    //界面点击扫描监听
                    if (fromBtn == true) {
                        if (result != null) {
                            if (!"".equals(str)) {
                                result.success(str);
                            } else {
                                result.notImplemented();
                            }
                        }
                        fromBtn = false;
                    } else {
                        //主动推扫描结果
                        if (sendChannel != null) {
                            sendChannel.send(str);
                        }
                    }
                }
            }
        }
    };

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        PdaPlugin.registrar = registrar;
        final MethodChannel channel = new MethodChannel(registrar.messenger(), pdaChannel);
        channel.setMethodCallHandler(new PdaPlugin());
        mContext = registrar.activity();
        boolean isinit = Settings.System.getInt(mContext.getContentResolver(), "scan_settings_left", 0) != 0;
        Log.e("wuyan", "isinit:" + isinit);
        if (isinit) {
            //扫描相关
            ScanSetting();

            //扫描广播注册
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(mBroadcastAction);
            registrar.activity().registerReceiver(receiver, intentFilter);

            //主动发送扫描结果
            sendChannel = new BasicMessageChannel<String>(
                    registrar.messenger(), pdaSendChannelName, StringCodec.INSTANCE);
            sendChannel.setMessageHandler(new BasicMessageChannel.MessageHandler() {
                @Override
                public void onMessage(Object o, BasicMessageChannel.Reply reply) {

                }
            });

            //识别车架号相关
            //测试系统有没有UHF服务（0是没有服务）
            int nType = Settings.System.getInt(mContext.getContentResolver(),
                    "uhf_type", 0);
            mService = UhfAdapter.getUhfManager(mContext.getApplicationContext());
            a = UhfAdapter.getUhfManager(mContext).getStatus();
            boolean isopen = mService.open();
            int b = 0;
            uhf_6c = (ISO1800_6C) mService.getISO1800_6C();
            DevBeep.init(mContext);
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        PdaPlugin.result = result;
        if (call.method.equals("startScan")) {//开始扫描
            fromBtn = true;
            Intent intent = new Intent();
            intent.setAction(mBroadscanAction);
            registrar.activity().sendBroadcast(intent);
        } else if (call.method.equals("stopScan")) {//销毁
            stopScan();
            result.notImplemented();
        } else if (call.method.equals("readRFIDCode")) {//识别车架号
            if (call.arguments != null) {
                isShowDialog = (boolean) call.arguments;
            }
            Log.d("wuyan", " Original  result " + result);
            if (recognizeComplete) {
                Toast.makeText(mContext, "识别中...，请勿频繁操作!", Toast.LENGTH_SHORT).show();
                return;
            }
            recognizeComplete = true;
            if (IsDoubClick()) {
                uhf_6c.inventory(callback);
            } else if (result != null) {
                result.notImplemented();
                recognizeComplete = false;
            }
        } else if (call.method.equals("writeRFIDCode")) {//制卡
            Map writeMap = (Map) call.arguments;
            vin = (String) writeMap.get("vin");
            if (vin.length() != 17) {
                Toast.makeText(mContext, "请输入17位Vin码!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (call.arguments != null) {
                isShowDialog = (boolean) writeMap.get("isNeedDialog");
            }
            if (IsDoubClick()) {
                uhf_6c.inventory(writeCallback);
            } else if (result != null) {
                result.notImplemented();
            }
        } else if (call.method.equals("print")) { //打印
            Map printData = (Map) call.arguments;
            String printDataString = new Gson().toJson(printData);
            PrintDto printDto = new Gson().fromJson(printDataString, PrintDto.class);
            mPrinter.SetGrayLevel((byte) 8);
            int printResult = mPrinter.Open();
            if (printResult == 0) {
                print(printDto);
                mPrinter.Close();
            }
        } else if (call.method.equals("goNextPage")) { //走纸一张
            mPrinter.GoToNextPage();
            int printResult = mPrinter.Open();
            if (printResult == 0) {
                mPrinter.GoToNextPage();
                mPrinter.Close();
            }
        } else if (call.method.equals("isPDA")) {
            boolean isPda = Settings.System.getInt(mContext.getContentResolver(), "scan_settings_left", 0) != 0;
            result.success(isPda);
        }
    }

    //扫描初始化
    private static void ScanSetting() {
        // 0 : fast; 1 : slow; 2 : broadcast
        String version = android.os.Build.VERSION.RELEASE;
        if (version.equals("4.2.2")) {
            scanmode = SysBarcodeUtil.getBarcodeSendMode(mContext);
            bleft = SysBarcodeUtil.getLeftSwitchState(mContext);
            bright = SysBarcodeUtil.getRightSwitchState(mContext);
            if (!bleft) {
                SysBarcodeUtil.setLeftSwitchState(mContext, true);
            }
            if (!bright) {
                SysBarcodeUtil.setRightSwitchState(mContext, true);
            }
            if (scanmode != 2) {
                SysBarcodeUtil.setBarcodeSendMode(mContext, 2);
            }
        } else {
            scanmode = ScanHelper.getBarcodeReceiveModel(mContext);
            bleft = ScanHelper.getScanSwitchLeft(mContext);
            bright = ScanHelper.getScanSwitchRight(mContext);
            bsound = ScanHelper.getScanSound(mContext);
            if (!bsound) {
                ScanHelper.setScanSound(mContext, true);
            }
            if (!bleft) {
                ScanHelper.setScanSwitchLeft(mContext, true);
            }
            if (!bright) {
                ScanHelper.setScanSwitchRight(mContext, true);
            }
            if (scanmode != 2)
                ScanHelper.setBarcodeReceiveModel(mContext, 2);
        }
    }

    //停止扫描
    private void stopScan() {
        registrar.activity().unregisterReceiver(receiver);
        String version = android.os.Build.VERSION.RELEASE;
        if (version.equals("4.2.2")) {
            SysBarcodeUtil.setLeftSwitchState(mContext, bleft);
            SysBarcodeUtil.setRightSwitchState(mContext, bright);
            SysBarcodeUtil.setBarcodeSendMode(mContext, scanmode);
        } else {
            ScanHelper.setScanSwitchLeft(mContext, bleft);
            ScanHelper.setScanSwitchRight(mContext, bright);
            ScanHelper.setBarcodeReceiveModel(mContext, scanmode);
            ScanHelper.setScanSound(mContext, bsound);
        }
    }

    //自定义排版格式打印
    private void print(PrintDto printDto) {
        for (int i = 0; i < printDto.getPrinter().size(); i++) {
            List<PrintDto.KeyBean> printLine = printDto.getPrinter().get(i);
            if (printLine == null) {
                printLine = new ArrayList<>();
            }
            mPrinter.PrintLineInit(35);
            for (int j = 0; j < printLine.size(); j++) {
                PrintDto.KeyBean keyBean = printLine.get(j);
                if (keyBean == null) {
                    keyBean = new PrintDto.KeyBean();
                }
                // "gravity" : 1 // 居1左、2、居中 3、居右
                if (keyBean.getGravity() == 1) {
                    mTitleType = printer.PrintType.Left;
                } else if (keyBean.getGravity() == 2) {
                    mTitleType = printer.PrintType.Centering;
                } else if (keyBean.getGravity() == 3) {
                    mTitleType = printer.PrintType.Right;
                }
                if (keyBean.getMode() == 1) {//文本
                    mPrinter.PrintLineStringByType(keyBean.getTitle(), keyBean.getFontSize(), keyBean.getMarginLeft(), keyBean.getMarginRight(), mTitleType, keyBean.isUnderLine(), keyBean.isBold());
                } else if (keyBean.getMode() == 2) {//条形码
                    Bitmap bm = null;
                    try {
                        bm = create1dBarcode(mOneBarcodeType, keyBean.getTitle(), keyBean.getBarWidth(), keyBean.getBarHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bm != null) {
                        //打印Bitmap 参数1：左边距，参数2：右边距，参数3：要打印的Bitmap
                        mPrinter.PrintBitmap(bm, keyBean.getMarginLeft(), keyBean.getMarginRight(), mTitleType);
                    }
                } else if (keyBean.getMode() == 3) {//二维码
                    Bitmap bm = null;
                    try {
                        bm = BarcodeUtil.encodeAsBitmap(keyBean.getTitle(),
                                BarcodeFormat.QR_CODE, keyBean.getBarWidth(), keyBean.getBarHeight());
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    if (bm != null) {
                        //打印Bitmap 参数1：左边距，参数2：右边距，参数3：要打印的Bitmap
                        mPrinter.PrintBitmap(bm, keyBean.getMarginLeft(), keyBean.getMarginRight(), mTitleType);
                    }
                } else {//图片
                    Bitmap bm = null;
                    if (keyBean.getByteMapPath() != null) {
                        bm = returnBitMap(keyBean.getByteMapPath());
                    }
                    if (bm != null) {
                        //打印Bitmap 参数1：左边距，参数2：右边距，参数3：要打印的Bitmap
                        mPrinter.PrintBitmap(bm, keyBean.getMarginLeft(), keyBean.getMarginRight(), mTitleType);
                    }
                }
            }
            //换行
            mPrinter.PrintLineEnd();
        }
        mPrinter.GoToNextPage();
    }

    //生成一维码
    private Bitmap create1dBarcode(String type, String content, int width, int height) {
        Bitmap bitmap = null;
        BarcodeFormat barcodeFormat = null;
        if ("128".equals(type)) {
            if (content.length() <= 128) {
                barcodeFormat = BarcodeFormat.CODE_128;
            }
        } else if ("EAN13".equals(type)) {
            if (content.length() == 13) {
                barcodeFormat = BarcodeFormat.EAN_13;
            }
        }
        if (barcodeFormat == null) {
            return bitmap;
        }
        try {
            bitmap = BarcodeUtil.create1dBarcode(content, barcodeFormat, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //将网络图片转换成bitmap
    private Bitmap returnBitMap(String url) {
        Log.i("returnBitMap", "url=" + url);
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //识别车架号、制卡相关
    private boolean IsDoubClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time > duration) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }

    //用于识别车架号
    IUhfCallback callback = new IUhfCallback.Stub() {
        @Override
        public void doInventory(final List<String> str) throws RemoteException {
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    if (str != null && str.size() > 0) {
                        String strEpc = "";
                        for (int i = 0; i < str.size(); i++) {
                            String strepc = str.get(0);
                            strEpc = strepc.substring(2, 6) + strepc.substring(6);
                            if (!"".equals(strEpc)) {
                                break;
                            }
                        }
                        Log.d("wuyan", " strEpc " + strEpc);
                        //单张
                        DevBeep.PlayOK();
                        Task task = new Task();
                        task.execute(strEpc);
                    } else if (result != null) {
                        result.notImplemented();
                        recognizeComplete = false;
                    }
                }
            });
        }

        @Override
        public void doTIDAndEPC(List<String> str) throws RemoteException {
        }
    };

    //用于制卡
    IUhfCallback writeCallback = new IUhfCallback.Stub() {
        @Override
        public void doInventory(final List<String> str) throws RemoteException {
            Handler mainThread = new Handler(Looper.getMainLooper());
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    if (str != null && str.size() > 0) {
                        String strEpc = "";
                        for (int i = 0; i < str.size(); i++) {
                            String strepc = str.get(0);
                            strEpc = strepc.substring(2, 6) + strepc.substring(6);
                            if (!"".equals(strEpc)) {
                                break;
                            }
                        }
                        Log.d("wuyan", "writeCallback strEpc " + strEpc);
                        //单张
                        DevBeep.PlayOK();
                        WriteTask writeTask = new WriteTask();
                        writeTask.execute(strEpc);
                    } else if (result != null) {
                        result.notImplemented();
                    }
                }
            });
        }

        @Override
        public void doTIDAndEPC(List<String> str) throws RemoteException {
        }
    };

    ProgressDialog dialog;

    //用于识别车架号
    private class Task extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("wuyan", " doInBackground input " + strings[0]);

            return Readlable(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isShowDialog) {
                //dialog开始
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
                dialog.setMessage("正在处理，请稍候...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected void onPostExecute(String o) {
            //dialog结束
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.d("wuyan", " onPostExecute result is " + o);
            if (result != null) {
                if (!"".equals(o)) {
                    result.success(o);
                } else {
                    result.success("");
                }
            }
            recognizeComplete = false;
        }
    }

    //用于制卡
    private class WriteTask extends AsyncTask<String, String, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            Log.d("wuyan", "WriteTask doInBackground input " + strings[0]);
            return Writelable(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isShowDialog) {
                //dialog开始
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
                dialog.setMessage("正在处理，请稍候...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected void onPostExecute(Integer o) {
            //dialog结束
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.d("wuyan", "WriteTask onPostExecute result is " + o);
            if (result != null) {
                result.success(o);
            }
        }
    }

    //用于识别车架号
    private String Readlable(String epcString) {
        Log.d("wuyan", " Readlable epcString " + epcString);
        long time = System.currentTimeMillis();
        //其实地址（word）默认
        int nadd = 2;
        //读取长度
        int ndatalen = 6;
        //密码（2word）
        String mimaStr = "00000000";
        byte[] passw = stringToBytes(mimaStr);
        //epc
        byte[] epc = stringToBytes(epcString);
        if (null != epc) {
            byte[] dataout = new byte[ndatalen * 2];
            if (btMemBank == 1) {
                int readData = uhf_6c.read(passw, epc.length, epc, (byte) btMemBank, nadd, ndatalen, dataout, 0, ndatalen);
                Log.d("wuyan", "read duration is " + (System.currentTimeMillis() - time) + " readData " + readData);
                if (readData == 0) {
                    String hexString = BytesToString(dataout, 0, ndatalen * 2);
                    //车架号 记得这里转换 TODO
                    String str = VinChangeUtil.str24To17(hexString);
                    Log.d("wuyan", "str24To17 str is " + str + " hexString " + hexString);
                    Log.d("wuyan", "str24To17 duration is " + (System.currentTimeMillis() - time));
                    return str;
                }

            }
        }
        return "";
    }

    //用于制卡
    private int Writelable(String epcString) {
        long time = System.currentTimeMillis();
        int nadd = 2;
        int ndatalen = 6;
        String mimaStr = "00000000";
        byte[] passw = stringToBytes(mimaStr);
        byte[] pwrite = new byte[ndatalen * 2];
        //vin 记得这里转换 TODO
        String dataE = VinChangeUtil.str17To24(vin.toUpperCase());
        Log.d("wuyan", "str17To24 duration is " + (System.currentTimeMillis() - time));
        byte[] myByte = stringToBytes(dataE);
        System.arraycopy(myByte, 0, pwrite, 0,
                myByte.length > ndatalen * 2 ? ndatalen * 2 : myByte.length);
        byte[] epc = stringToBytes(epcString);
        int iswrite = uhf_6c.write(passw, epc.length, epc, btMemBank, (byte) nadd, (byte) ndatalen * 2, pwrite);
        Log.d("wuyan", "write duration is " + (System.currentTimeMillis() - time) + " iswrite " + iswrite);
        return iswrite;
    }

    public static byte[] stringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public String BytesToString(byte[] b, int nS, int ncount) {
        String ret = "";
        int nMax = ncount > (b.length - nS) ? b.length - nS : ncount;
        for (int i = 0; i < nMax; i++) {
            String hex = Integer.toHexString(b[i + nS] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
}

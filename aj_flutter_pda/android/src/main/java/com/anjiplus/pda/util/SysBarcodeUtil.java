/**
 * use permission:
 * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 */
package com.anjiplus.pda.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author wpx
 */
public class SysBarcodeUtil {
    private static final String BARCODE_SETTINGS_LEFT = "barcode_settings_left";
    private static final String BARCODE_SETTINGS_LEFT_SELECTED = "barcode_settings_left_selected";
    private static final String BARCODE_SETTINGS_RIGHT = "barcode_settings_right";
    private static final String BARCODE_SETTINGS_RIGHT_SELECTED = "barcode_settings_right_selected";
    private static final String BARCODE_TYPE = "barcode_typemessage_settings";
    private static final String BARCODE_INTERVAL_SETTINGS = "BARCODE_INTERVAL_SETTINGS";
    private static final String BARCODE_SENDMESSAGE_SETTINGS = "barcode_sendmessage_settings";
    private static final String BARCODE_NEWPARAGRAPH_SETTINGS = "barcode_newparagraph_settings";
    /**
     *
     */
    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";
    /**
     *
     */
    private static final String BARCODE_PARAM = "BARCODE";
    /**
     *
     */
    private static Object object;
    /**
     *
     */
    private static BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BARCODE_ACTION.equals(action)) {
                String barcode = intent.getStringExtra(BARCODE_PARAM);
                if (object == null) {
                    Toast.makeText(context, "Object is null.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (object instanceof EditText) {
                    ((EditText) object).setText(barcode);
                } else if (object instanceof TextView) {
                    ((TextView) object).setText(barcode);
                } else {
                    Toast.makeText(context, "Object is not fit.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private static SysBarcodeUtil Instance = null;
    private static Context mContext = null;

    private SysBarcodeUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @param obj
     * @return
     */
    public static BroadcastReceiver getBarcodeReceiver(Object obj) {
        object = obj;
        return barcodeReceiver;
    }

    /**
     * @return
     */
    public static IntentFilter getBarcodeFilter() {
        return new IntentFilter(BARCODE_ACTION);
    }

    public static SysBarcodeUtil getInstance(Context context) {
        if (Instance == null) {
            Instance = new SysBarcodeUtil();
        }
        mContext = context;
        return Instance;
    }

    /**
     * get left scan key state  0 : close , 1 : open
     *
     * @return
     */
    public static boolean getLeftSwitchState(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_SETTINGS_LEFT, 0) != 0;
    }

    /**
     * set left scan state
     *
     * @param flag false : 0, true : 1
     */
    public static void setLeftSwitchState(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_SETTINGS_LEFT, flag ? 1 : 0);
    }

    /**
     * get left scan selected(hardware or camera)
     *
     * @return 0 : hardware, 1 : camera
     */
    public static int getLeftScanSelected(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_SETTINGS_LEFT_SELECTED, 0);
    }

    /**
     * set left scan selected(hardware or camera)
     * type 0 : hardware, 1 : camera
     */
    public static void setLeftScanSelected(Context context, int type) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_SETTINGS_LEFT_SELECTED, type);
    }

    /**
     * set right scan key state  0 : close , 1 : open
     *
     * @return
     */
    public static boolean getRightSwitchState(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_SETTINGS_RIGHT, 0) != 0;
    }

    /**
     * get right scan state
     *
     * @param flag false : 0, true : 1
     */
    public static void setRightSwitchState(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_SETTINGS_RIGHT, flag ? 1 : 0);
    }

    /**
     * get right scan selected(hardware or camera)
     *
     * @return 0 : hardware, 1 : camera
     */
    public static int getRightScanSelected(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_SETTINGS_RIGHT_SELECTED, 0);
    }

    /**
     * set left scan selected(hardware or camera)
     * type 0 : hardware, 1 : camera
     */
    public static void setRightScanSelected(Context context, int type) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_SETTINGS_RIGHT_SELECTED, type);
    }

    /**
     * get send barcode type
     *
     * @return Mode 0 normal,2 repeat
     */
    public static int getBarcodeType(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_TYPE, 0);
    }

    /**
     * set send barcode interval
     *
     * @param Mode
     */
    public static void setBarcodeInterval(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_INTERVAL_SETTINGS, mode);
    }

    /**
     * get send barcode interval
     *
     * @return
     */
    public static int getBarcodeInterval(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_INTERVAL_SETTINGS, 30000);
    }

    /**
     * set send barcode type
     *
     * @param Mode 0 normal,2 repeat
     */
    public static void setBarcodeType(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_TYPE, mode);
    }

    /**
     * get send barcode mode
     *
     * @return Mode 0 is fast,1 is slow,2 is broadcast
     */
    public static int getBarcodeSendMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_SENDMESSAGE_SETTINGS, 0);
    }

    /**
     * set send barcode mode
     *
     * @param mode Send Mode 0 is fast,1 is slow,2 is broadcast
     */
    public static void setBarcodeSendMode(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_SENDMESSAGE_SETTINGS, mode);
    }

    /**
     * get newlins way mode
     *
     * @return 0 : "Enter newline" ; 1 : "\n newline"; 2 : "no newline"
     */
    public static int getBarcodeNewParagraphMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                BARCODE_NEWPARAGRAPH_SETTINGS, 0);
    }

    /**
     * set newlines way mode 0 : "Enter newline" ; 1 : "\n newline"; 2 : "no newline"
     *
     * @param mode
     */
    public static void setBarcodeNewParagraphMode(Context context, int mode) {
        Settings.System.putInt(context.getContentResolver(),
                BARCODE_NEWPARAGRAPH_SETTINGS, mode);
    }

    /**
     * @return
     */
    public String getSysBarcodeDefaultValue() {
        StringBuilder strValues = new StringBuilder();
        strValues.append(getLeftSwitchState(mContext)).append(",");
        strValues.append(getLeftScanSelected(mContext)).append(",");
        strValues.append(getRightSwitchState(mContext)).append(",");
        strValues.append(getRightScanSelected(mContext)).append(",");
        strValues.append(getBarcodeSendMode(mContext)).append(",");
        strValues.append(getBarcodeNewParagraphMode(mContext));

        return strValues.toString();
    }

    /**
     * @param LeftSwitchState         false : 0, true : 1
     * @param LeftScanSelected        0 : hardware, 1 : camera
     * @param RightSwitchState        false : 0, true : 1
     * @param RightScanSelected       0 : hardware, 1 : camera
     * @param BarcodeSendMode         0 : fast, 1 : slow, 2 : broadcast
     * @param BarcodeNewParagraphMode 0 : "Enter newline" ; 1 : "\n newline"; 2 : "no newline"
     */
    public void setSysBarcodeValue(boolean LeftSwitchState,
                                   int LeftScanSelected,
                                   boolean RightSwitchState,
                                   int RightScanSelected,
                                   int BarcodeSendMode,
                                   int BarcodeNewParagraphMode) {

        setLeftSwitchState(mContext, LeftSwitchState);
        setLeftScanSelected(mContext, LeftScanSelected);
        setRightSwitchState(mContext, RightSwitchState);
        setRightScanSelected(mContext, RightScanSelected);
        setBarcodeSendMode(mContext, BarcodeSendMode);
        setBarcodeNewParagraphMode(mContext, BarcodeNewParagraphMode);

        sendBroadcastSysBarcode();
    }

    /**
     *
     */
    public void sendBroadcastSysBarcode() {
        Intent intent = new Intent("SysScanSettingChanged");
        mContext.sendBroadcast(intent);
    }
}

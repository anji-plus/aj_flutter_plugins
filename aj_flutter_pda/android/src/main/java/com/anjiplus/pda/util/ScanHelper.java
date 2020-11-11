package com.anjiplus.pda.util;

import android.content.Context;
import android.provider.Settings;

public class ScanHelper {
    /**
     * scan settings show 0 : no; 1 : yes
     */
    public static final String SCAN_SETTINGS_HAVED = "scan_settings_haved";
    /**
     * scan settings left enable 0 : no; 1 : yes
     */
    public static final String SCAN_SETTINGS_LEFT = "scan_settings_left";
    /**
     * scan settings right enable 0 : no; 1 : yes
     */
    public static final String SCAN_SETTINGS_RIGHT = "scan_settings_right";
    /**
     * scan models settings 0 : noraml; 1 : continue auto; 2 : continue manual
     */
    public static final String SCAN_MODLES_SETTINGS = "scan_models_settings";
    /**
     * scan continue delay settings 0 : none; 1 : 0.5s; 2 : 1s; 3 : custom
     */
    public static final String SCAN_CONTINUE_DELAY_SETTINGS = "scan_continue_delay_settings";
    /**
     * scan continue delay value
     */
    public static final String SCAN_CONTINUE_DELAY_CUSTOM_VALUE = "scan_continue_delay_custom_value";
    /**
     * barcode receive model 0 : fast; 1 : slow; 2 : broadcast
     */
    public static final String BARCODE_RECEIVE_MODELS_SETTINGS = "barcode_receive_models_settings";
    /**
     * barcode separator model 0 : none; 1 : '\n'; 2 : Enter
     */
    public static final String BARCODE_SEPARATOR_SETTINGS = "barcode_separator_settings";
    /**
     * barcode separator prefix settings 0 : no; 1 : yes
     */
    public static final String BARCODE_SEPARATOR_PREFIX_SETTINGS = "barcode_separator_prefix_settings";
    /**
     * barcode separator prefix content
     */
    public static final String BARCODE_SEPARATOR_PREFIX = "barcode_separator_prefix";
    /**
     * barcode separator suffix settings 0 : no; 1 : yes
     */
    public static final String BARCODE_SEPARATOR_SUFFIX_SETTINGS = "barcode_separator_suffix_settings";
    /**
     * barcode separator suffix content
     */
    public static final String BARCODE_SEPARATOR_SUFFIX = "barcode_separator_suffix";
    /**
     * scan sound settings 0 : no; 1 : yes
     */
    public static final String SCAN_SOUND_SETTINGS = "scan_sound_settings";
    /**
     * scan vibrate settings 0 : no; 1 : yes
     */
    public static final String SCAN_VIBRATE_SETTINGS = "scan_vibrate_settings";

    public static boolean getScanToggleState(Context context) {
        boolean flag = false;
        boolean scan_left = Settings.System.getInt(context.getContentResolver(),
                SCAN_SETTINGS_LEFT, 0) != 0;
        boolean scan_right = Settings.System.getInt(context.getContentResolver(),
                SCAN_SETTINGS_RIGHT, 0) != 0;
        if (scan_left != false || scan_right != false) {
            flag = true;
        }
        return flag;
    }

    /**
     * scan left state
     *
     * @param context
     * @return
     */
    public static boolean getScanSwitchLeft(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_SETTINGS_LEFT, 0) != 0;
    }

    /**
     * set scan left state
     *
     * @param context
     * @param flag
     */
    public static void setScanSwitchLeft(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), SCAN_SETTINGS_LEFT, flag ? 1 : 0);
    }

    /**
     * scan right state
     *
     * @param context
     * @return
     */
    public static boolean getScanSwitchRight(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_SETTINGS_RIGHT, 0) != 0;
    }

    /**
     * set scan right state
     *
     * @param context
     * @param flag
     */
    public static void setScanSwitchRight(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), SCAN_SETTINGS_RIGHT, flag ? 1 : 0);
    }

    /**
     * scan model state
     *
     * @param context
     * @return 0 : normal; 1 : continue auto; 2 : continue manual
     */
    public static int getScanModel(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_MODLES_SETTINGS, 0);
    }

    /**
     * set scan model state
     *
     * @param context
     * @param flag    0 : normal; 1 : continue auto; 2 : continue manual
     */
    public static void setScanModel(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(), SCAN_MODLES_SETTINGS, state);
    }

    /**
     * scan delay state
     *
     * @param context
     * @return 0 : none; 1 : 0.5s; 2 : 1s;3 : custom delay
     */
    public static int getScanDelaySetting(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_CONTINUE_DELAY_SETTINGS, 0);
    }

    /**
     * set scan delay state
     *
     * @param context
     * @param flag    0 : none; 1 : 0.5s; 2 : 1s;3 : custom delay
     */
    public static void setScanDelaySetting(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(), SCAN_CONTINUE_DELAY_SETTINGS, state);
    }

    /**
     * scan delay value
     *
     * @param context
     * @return delay
     */
    public static float getScanDelay(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), SCAN_CONTINUE_DELAY_CUSTOM_VALUE, 0);
    }

    /**
     * set scan delay value
     *
     * @param context
     * @param delay
     */
    public static void setScanDelay(Context context, float delay) {
        Settings.System.putFloat(context.getContentResolver(), SCAN_CONTINUE_DELAY_CUSTOM_VALUE, delay);
    }

    /**
     * barcode receive model state
     *
     * @param context
     * @return 0 : fast; 1 : slow; 2 : broadcast
     */
    public static int getBarcodeReceiveModel(Context context) {
        return Settings.System.getInt(context.getContentResolver(), BARCODE_RECEIVE_MODELS_SETTINGS, 0);
    }

    /**
     * set broadcast receive model state
     *
     * @param context
     * @param flag    0 : fast; 1 : slow; 2 : broadcast
     */
    public static void setBarcodeReceiveModel(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(), BARCODE_RECEIVE_MODELS_SETTINGS, state);
    }

    /**
     * barcode separator state
     *
     * @param context
     * @return 0 : none; 1 : newline; 2 : enter
     */
    public static int getBarcodeSeparator(Context context) {
        return Settings.System.getInt(context.getContentResolver(), BARCODE_SEPARATOR_SETTINGS, 0);
    }

    /**
     * set barcode separator state
     *
     * @param context
     * @param flag    0 : none; 1 : newline; 2 : enter
     */
    public static void setBarcodeSeparator(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(), BARCODE_SEPARATOR_SETTINGS, state);
    }

    /**
     * barcode Prefix state
     *
     * @param context
     * @return 0 : close; 1 : open
     */
    public static boolean getBarcodePrefixState(Context context) {
        return Settings.System.getInt(context.getContentResolver(), BARCODE_SEPARATOR_PREFIX_SETTINGS, 0) != 0;
    }

    /**
     * set barcode Prefix state
     *
     * @param context
     * @param flag    0 : close; 1 : open
     */
    public static void setBarcodePrefixState(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), BARCODE_SEPARATOR_PREFIX_SETTINGS, flag ? 1 : 0);
    }

    /**
     * barcode Prefix
     *
     * @param context
     * @return
     */
    public static String getBarcodePrefix(Context context) {
        return Settings.System.getString(context.getContentResolver(), BARCODE_SEPARATOR_PREFIX);
    }

    /**
     * set barcode Prefix
     *
     * @param context
     * @param
     */
    public static void setBarcodePrefix(Context context, String prefix) {
        Settings.System.putString(context.getContentResolver(), BARCODE_SEPARATOR_PREFIX, prefix);
    }

    /**
     * barcode Suffix state
     *
     * @param context
     * @return 0 : close; 1 : open
     */
    public static boolean getBarcodeSuffixState(Context context) {
        return Settings.System.getInt(context.getContentResolver(), BARCODE_SEPARATOR_SUFFIX_SETTINGS, 0) != 0;
    }

    /**
     * set barcode Suffix state
     *
     * @param context
     * @param flag    0 : close; 1 : open
     */
    public static void setBarcodeSuffixState(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), BARCODE_SEPARATOR_SUFFIX_SETTINGS, flag ? 1 : 0);
    }

    /**
     * barcode Suffix
     *
     * @param context
     * @return
     */
    public static String getBarcodeSuffix(Context context) {
        return Settings.System.getString(context.getContentResolver(), BARCODE_SEPARATOR_SUFFIX);
    }

    /**
     * set barcode Suffix
     *
     * @param context
     * @param
     */
    public static void setBarcodeSuffix(Context context, String suffix) {
        Settings.System.putString(context.getContentResolver(), BARCODE_SEPARATOR_SUFFIX, suffix);
    }

    /**
     * scan sound state
     *
     * @param context
     * @return
     */
    public static boolean getScanSound(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_SOUND_SETTINGS, 0) != 0;
    }

    /**
     * set scan sound state
     *
     * @param context
     * @param flag
     */
    public static void setScanSound(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), SCAN_SOUND_SETTINGS, flag ? 1 : 0);
    }

    /**
     * scan Vibrate state
     *
     * @param context
     * @return
     */
    public static boolean getScanVibrate(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SCAN_VIBRATE_SETTINGS, 0) != 0;
    }

    /**
     * set scan Vibrate state
     *
     * @param context
     * @param flag
     */
    public static void setScanVibrate(Context context, boolean flag) {
        Settings.System.putInt(context.getContentResolver(), SCAN_VIBRATE_SETTINGS, flag ? 1 : 0);
    }
}

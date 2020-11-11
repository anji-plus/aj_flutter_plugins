// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package anjiplus.aj.flutter.aj_flutter_scan;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.yanzhenjie.zbar.Image;
import com.yanzhenjie.zbar.ImageScanner;
import com.yanzhenjie.zbar.Symbol;
import com.yanzhenjie.zbar.SymbolSet;
import com.zbar.lib.CaptureActivity;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import aj.flutter.scan.R;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A delegate class doing the heavy lifting for the plugin.
 *
 * <p>1. Check for an existing {@link #pendingResult}. If a previous pendingResult exists, this
 * means that the chooseImageFromGallery() or takeImageWithCamera() method was called at least
 * twice. In this case, stop executing and finish with an error.
 *
 * <p>2. Check that a required runtime permission has been granted. The chooseImageFromGallery()
 * method checks if the {@link Manifest.permission#READ_EXTERNAL_STORAGE} permission has been
 * granted. Similarly, the takeImageWithCamera() method checks that {@link
 * Manifest.permission#CAMERA} has been granted.
 *
 * <p>The permission check can end up in two different outcomes:
 *
 * <p>A) If the permission has already been granted, continue with picking the image from gallery or
 * camera.
 *
 * <p>B) If the permission hasn't already been granted, ask for the permission from the user. If the
 * user grants the permission, proceed with step #3. If the user denies the permission, stop doing
 * anything else and finish with a null result.
 *
 * <p>3. Launch the gallery or camera for picking the image, depending on whether
 * chooseImageFromGallery() or takeImageWithCamera() was called.
 *
 * <p>This can end up in three different outcomes:
 *
 * <p>A) User picks an image. No maxWidth or maxHeight was specified when calling {@code
 * pickImage()} method in the Dart side of this plugin. Finish with full path for the picked image
 * as the result.
 *
 * <p>B) User picks an image. A maxWidth and/or maxHeight was provided when calling {@code
 * pickImage()} method in the Dart side of this plugin. A scaled copy of the image is created.
 * Finish with full path for the scaled image as the result.
 *
 * <p>C) User cancels picking an image. Finish with null result.
 */
public class ImagePickerDelegate
        implements PluginRegistry.ActivityResultListener,
        PluginRegistry.RequestPermissionsResultListener {
    @VisibleForTesting
    static final int REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY = 2242;
    @VisibleForTesting
    static final int REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA = 2243;
    @VisibleForTesting
    public static int REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA_ERROR = 2244;
    @VisibleForTesting
    static final int REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 2245;
    @VisibleForTesting
    static final int REQUEST_CAMERA_IMAGE_PERMISSION = 2246;
    @VisibleForTesting
    final String fileProviderName;

    private final Activity activity;
    private final File externalFilesDirectory;
    private final ImageResizer imageResizer;
    private final PermissionManager permissionManager;
    private final FileUtils fileUtils;
    //震动和声音
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;

    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;
    private ProgressDialog dialog;

    interface PermissionManager {
        boolean isPermissionGranted(String permissionName);

        void askForPermission(String permissionName, int requestCode);

        boolean needRequestCameraPermission();
    }

    interface IntentResolver {
        boolean resolveActivity(Intent intent);
    }

    interface FileUriResolver {
        Uri resolveFileProviderUriForFile(String fileProviderName, File imageFile);

        void getFullImagePath(Uri imageUri, OnPathReadyListener listener);
    }

    interface OnPathReadyListener {
        void onPathReady(String path);
    }

    public ImagePickerDelegate(
            final Activity activity, File externalFilesDirectory, ImageResizer imageResizer) {
        this(
                activity,
                externalFilesDirectory,
                imageResizer,
                null,
                null,
                new PermissionManager() {
                    @Override
                    public boolean isPermissionGranted(String permissionName) {
                        return ActivityCompat.checkSelfPermission(activity, permissionName)
                                == PackageManager.PERMISSION_GRANTED;
                    }

                    @Override
                    public void askForPermission(String permissionName, int requestCode) {
                        ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
                    }

                    @Override
                    public boolean needRequestCameraPermission() {
                        return ImagePickerUtils.needRequestCameraPermission(activity);
                    }
                },
                new IntentResolver() {
                    @Override
                    public boolean resolveActivity(Intent intent) {
                        return intent.resolveActivity(activity.getPackageManager()) != null;
                    }
                },
                new FileUriResolver() {
                    @Override
                    public Uri resolveFileProviderUriForFile(String fileProviderName, File file) {
                        return FileProvider.getUriForFile(activity, fileProviderName, file);
                    }

                    @Override
                    public void getFullImagePath(final Uri imageUri, final OnPathReadyListener listener) {
                        MediaScannerConnection.scanFile(
                                activity,
                                new String[]{(imageUri != null) ? imageUri.getPath() : ""},
                                null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        listener.onPathReady(path);
                                    }
                                });
                    }
                },
                new FileUtils());
    }

    /**
     * This constructor is used exclusively for testing; it can be used to provide mocks to final
     * fields of this class. Otherwise those fields would have to be mutable and visible.
     */
    @VisibleForTesting
    ImagePickerDelegate(
            Activity activity,
            File externalFilesDirectory,
            ImageResizer imageResizer,
            MethodChannel.Result result,
            MethodCall methodCall,
            PermissionManager permissionManager,
            IntentResolver intentResolver,
            FileUriResolver fileUriResolver,
            FileUtils fileUtils) {
        this.activity = activity;
        this.externalFilesDirectory = externalFilesDirectory;
        this.imageResizer = imageResizer;
        this.fileProviderName = activity.getPackageName() + ".flutter.image_provider";
        this.pendingResult = result;
        this.methodCall = methodCall;
        this.permissionManager = permissionManager;
        this.fileUtils = fileUtils;
        //声音&震动
        playBeep = true;
        vibrate = true;
        AudioManager audioService = (AudioManager) activity.getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();

    }

    private boolean isflag = false;//从扫描的页面进入相册时别

    void retrieveLostImage(MethodChannel.Result result) {
        Map<String, Object> resultMap = ImagePickerCache.getCacheMap();
        String path = (String) resultMap.get(ImagePickerCache.MAP_KEY_PATH);
        if (path != null) {
            Double maxWidth = (Double) resultMap.get(ImagePickerCache.MAP_KEY_MAX_WIDTH);
            Double maxHeight = (Double) resultMap.get(ImagePickerCache.MAP_KEY_MAX_HEIGHT);
            String newPath = imageResizer.resizeImageIfNeeded(activity, path, maxWidth, maxHeight);
            resultMap.put(ImagePickerCache.MAP_KEY_PATH, newPath);
        }
        if (resultMap.isEmpty()) {
            result.success(null);
        } else {
            result.success(resultMap);
        }
        ImagePickerCache.clear();
    }

    //相机
    public void chooseImageFromCamera(MethodCall methodCall, MethodChannel.Result result) {
        setPendingMethodCallAndResult(methodCall, result);
        if (needRequestCameraPermission()
                && !permissionManager.isPermissionGranted(Manifest.permission.CAMERA)) {
            permissionManager.askForPermission(
                    Manifest.permission.CAMERA, REQUEST_CAMERA_IMAGE_PERMISSION);
            return;
        }
        launchTakeImageWithCameraIntent();
    }

    //相册
    public void chooseImageFromGallery(MethodCall methodCall, MethodChannel.Result result) {
        setPendingMethodCallAndResult(methodCall, result);
        if (!permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION);
            return;
        }

        launchPickImageFromGalleryIntent();
    }

    //相册
    public void chooseImageFromGallery() {
        isflag = true;
        launchPickImageFromGalleryIntent();
    }

    private boolean needRequestCameraPermission() {
        if (permissionManager == null) {
            return false;
        }
        return permissionManager.needRequestCameraPermission();
    }

    private void launchTakeImageWithCameraIntent() {
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("scan_title", (String) methodCall.argument("scan_title"));
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA);
    }

    private void launchPickImageFromGalleryIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");

        activity.startActivityForResult(pickImageIntent, REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY);

    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted =
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case REQUEST_CAMERA_IMAGE_PERMISSION:
                if (permissionGranted) {
                    launchTakeImageWithCameraIntent();
                }
                break;
            case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                if (permissionGranted) {
                    launchPickImageFromGalleryIntent();
                }
                break;
            default:
                return false;
        }

        if (!permissionGranted) {
            switch (requestCode) {
                case REQUEST_CAMERA_IMAGE_PERMISSION:
                    finishWithError("camera_access_denied", "The user did not allow camera access.");
                    break;
                case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                    finishWithError("photo_access_denied", "The user did not allow photo access.");
                    break;

            }
        }

        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        //从扫描页进入到相册中选择图片识别
        if (REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA == requestCode && isflag) {
            isflag = false;
            return false;
        }
        //从相机识别，这里无需异步
        if (REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA == requestCode) {
            String code = data.getStringExtra("resultCode");
            if (resultCode == 1) {
                if (pendingResult != null) {
                    pendingResult.success(code);
                }
            } else if (REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA_ERROR == resultCode) {
                if (pendingResult != null) {
                    pendingResult.error(code, null, null);
                }
            }
            return true;
        } else if (REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY == requestCode && resultCode == RESULT_OK) {
            //从相册识别，图片要处理，所以要异步
            GalleryHandleTask GalleryHandleTask = new GalleryHandleTask(resultCode, data);
            GalleryHandleTask.execute();
            return true;
        }
        return false;
    }

    private void handleChooseImageResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String path = fileUtils.getPathFromUri(activity, data.getData());
            handleGalleryResult(path, false);
            return;
        }

        // User cancelled choosing a Gallery.
        finishWithSuccess(null);
    }

    private void handleGalleryResult(String path, boolean shouldDeleteOriginalIfScaled) {
        if (methodCall != null) {
            Double maxWidth = methodCall.argument("maxWidth");
            Double maxHeight = methodCall.argument("maxHeight");
            Double compressSize = methodCall.argument("compressSize");
            //2 裁剪尺寸
            String screenImagePath = imageResizer.resizeImageIfNeeded(activity, path, maxWidth, maxHeight);
            //3 压缩大小
            String finalImagePath = PhotoBitmapUtils.getCompressPhotoUrl(screenImagePath, compressSize);
            finishWithSuccess(finalImagePath);

            //delete original file if scaled
            if (!finalImagePath.equals(path) && shouldDeleteOriginalIfScaled) {
                new File(path).delete();
            }
        } else {
            finishWithSuccess(path);
        }
    }

    //异步任务
    public class GalleryHandleTask extends AsyncTask {

        private int resultCode;
        private Intent data;

        public GalleryHandleTask(int resultCode, Intent data) {
            this.resultCode = resultCode;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setMessage("正在处理，请稍候...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            handleChooseImageResult(resultCode, data);
            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.dismiss();
        }

    }

    private boolean setPendingMethodCallAndResult(
            MethodCall methodCall, MethodChannel.Result result) {
        this.methodCall = methodCall;
        pendingResult = result;

        // Clean up cache if a new image picker is launched.
        ImagePickerCache.clear();

        return true;
    }

    private void finishWithSuccess(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        bitmap = Bitmap.createScaledBitmap(bitmap, activity.getResources().getDisplayMetrics().widthPixels, activity.getResources().getDisplayMetrics().heightPixels, false);
        Image barcode = null;
        try {
            int picWidth = bitmap.getWidth();
            int picHeight = bitmap.getHeight();
            barcode = new Image(picWidth, picHeight, "RGB4");
            int[] pix = new int[picWidth * picHeight];
            bitmap.getPixels(pix, 0, picWidth, 0, 0, picWidth, picHeight);
            barcode.setData(pix);
            barcode = barcode.convert("Y800");
        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        }
        String result = "";
        ImageScanner mImageScanner = new ImageScanner();
        int code = mImageScanner.scanImage(barcode);
        if (code != 0) {
            SymbolSet symSet = mImageScanner.getResults();
            for (Symbol sym : symSet)
                result = sym.getData();
        }
        playBeepSoundAndVibrate();
        pendingResult.success(result);
        clearMethodCallAndResult();
    }

    private void finishWithAlreadyActiveError(MethodChannel.Result result) {
        result.error("already_active", "Image picker is already active", null);
    }

    private void finishWithError(String errorCode, String errorMessage) {
        if (pendingResult == null) {
            ImagePickerCache.saveResult(null, errorCode, errorMessage);
            return;
        }
        pendingResult.error(errorCode, errorMessage, null);
        clearMethodCallAndResult();
    }

    private void clearMethodCallAndResult() {
        methodCall = null;
        pendingResult = null;
    }

    //声音&震动
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = activity.getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

}

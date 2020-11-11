// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static android.app.Activity.RESULT_OK;

/**
 * A delegate class doing the heavy lifting for the plugin.
 *
 * <p>When invoked, both the {@link #chooseImageFromGallery} and {@link #takeImageWithCamera}
 * methods go through the same steps:
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
    static final int REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY = 2342;
    @VisibleForTesting
    static final int REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA = 2343;
    @VisibleForTesting
    static final int REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 2344;
    @VisibleForTesting
    static final int REQUEST_CAMERA_IMAGE_PERMISSION = 2345;
    @VisibleForTesting
    static final int REQUEST_CODE_CHOOSE_VIDEO_FROM_GALLERY = 2352;
    @VisibleForTesting
    static final int REQUEST_CODE_TAKE_VIDEO_WITH_CAMERA = 2353;
    @VisibleForTesting
    static final int REQUEST_EXTERNAL_VIDEO_STORAGE_PERMISSION = 2354;
    @VisibleForTesting
    static final int REQUEST_CAMERA_VIDEO_PERMISSION = 2355;

    @VisibleForTesting
    final String fileProviderName;

    private final Activity activity;
    private final File externalFilesDirectory;
    private final ImageResizer imageResizer;
    private final PermissionManager permissionManager;
    private final IntentResolver intentResolver;
    private final FileUriResolver fileUriResolver;
    private final FileUtils fileUtils;

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

    private Uri pendingCameraMediaUri;
    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;

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
        this.intentResolver = intentResolver;
        this.fileUriResolver = fileUriResolver;
        this.fileUtils = fileUtils;
    }

    void saveStateBeforeResult() {
        if (methodCall == null) {
            return;
        }

        ImagePickerCache.saveTypeWithMethodCallName(methodCall.method);
        ImagePickerCache.saveDemensionWithMethodCall(methodCall);
        if (pendingCameraMediaUri != null) {
            ImagePickerCache.savePendingCameraMediaUriPath(pendingCameraMediaUri);
        }
    }

    void retrieveLostImage(MethodChannel.Result result) {
        Map<String, Object> resultMap = ImagePickerCache.getCacheMap();
        String path = (String) resultMap.get(ImagePickerCache.MAP_KEY_PATH);
        if (path != null) {
            Double maxWidth = (Double) resultMap.get(ImagePickerCache.MAP_KEY_MAX_WIDTH);
            Double maxHeight = (Double) resultMap.get(ImagePickerCache.MAP_KEY_MAX_HEIGHT);
            Bitmap bitmap = imageResizer.resizedBitmap(activity, path, maxWidth, maxHeight);
            if (bitmap == null) {
                if (pendingResult != null) {
                    pendingResult.notImplemented();
                }
                return;
            }
            String newPath = PhotoBitmapUtils.getCompressPhotoUrl(externalFilesDirectory, bitmap, path, 0d);
            resultMap.put(ImagePickerCache.MAP_KEY_PATH, newPath);
        }
        if (resultMap.isEmpty()) {
            result.success(null);
        } else {
            result.success(resultMap);
        }
        ImagePickerCache.clear();
    }

    public void chooseVideoFromGallery(MethodCall methodCall, MethodChannel.Result result) {
        if (!setPendingMethodCallAndResult(methodCall, result)) {
            finishWithAlreadyActiveError(result);
            return;
        }

        if (!permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_EXTERNAL_VIDEO_STORAGE_PERMISSION);
            return;
        }

        launchPickVideoFromGalleryIntent();
    }

    private void launchPickVideoFromGalleryIntent() {
        Intent pickVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickVideoIntent.setType("video/*");

        activity.startActivityForResult(pickVideoIntent, REQUEST_CODE_CHOOSE_VIDEO_FROM_GALLERY);
    }

    public void takeVideoWithCamera(MethodCall methodCall, MethodChannel.Result result) {
        if (!setPendingMethodCallAndResult(methodCall, result)) {
            finishWithAlreadyActiveError(result);
            return;
        }

        if (needRequestCameraPermission()
                && !permissionManager.isPermissionGranted(Manifest.permission.CAMERA)) {
            permissionManager.askForPermission(
                    Manifest.permission.CAMERA, REQUEST_CAMERA_VIDEO_PERMISSION);
            return;
        }

        launchTakeVideoWithCameraIntent();
    }

    private void launchTakeVideoWithCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        boolean canTakePhotos = intentResolver.resolveActivity(intent);

        if (!canTakePhotos) {
            finishWithError("no_available_camera", "No cameras available for taking pictures.");
            return;
        }

        File videoFile = createTemporaryWritableVideoFile();
        pendingCameraMediaUri = Uri.parse("file:" + videoFile.getAbsolutePath());

        Uri videoUri = fileUriResolver.resolveFileProviderUriForFile(fileProviderName, videoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        grantUriPermissions(intent, videoUri);

        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO_WITH_CAMERA);
    }

    public void chooseImageFromGallery(MethodCall methodCall, MethodChannel.Result result) {
        if (!setPendingMethodCallAndResult(methodCall, result)) {
            finishWithAlreadyActiveError(result);
            return;
        }

        if (!permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION);
            return;
        }

        launchPickImageFromGalleryIntent();
    }

    private void launchPickImageFromGalleryIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");
        activity.startActivityForResult(pickImageIntent, REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY);
    }

    public void takeImageWithCamera(MethodCall methodCall, MethodChannel.Result result) {
        if (!setPendingMethodCallAndResult(methodCall, result)) {
            finishWithAlreadyActiveError(result);
            return;
        }

        if (needRequestCameraPermission()
                && !permissionManager.isPermissionGranted(Manifest.permission.CAMERA)) {
            permissionManager.askForPermission(
                    Manifest.permission.CAMERA, REQUEST_CAMERA_IMAGE_PERMISSION);
            return;
        }

        launchTakeImageWithCameraIntent();
    }

    private boolean needRequestCameraPermission() {
        if (permissionManager == null) {
            return false;
        }
        return permissionManager.needRequestCameraPermission();
    }

    private void launchTakeImageWithCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhotos = intentResolver.resolveActivity(intent);

        if (!canTakePhotos) {
            finishWithError("no_available_camera", "No cameras available for taking pictures.");
            return;
        }

        File imageFile = createTemporaryWritableImageFile();
        pendingCameraMediaUri = Uri.parse("file:" + imageFile.getAbsolutePath());

        Uri imageUri = fileUriResolver.resolveFileProviderUriForFile(fileProviderName, imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        grantUriPermissions(intent, imageUri);

        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA);
    }

    private File createTemporaryWritableImageFile() {
        return createTemporaryWritableFile(".jpg");
    }

    private File createTemporaryWritableVideoFile() {
        return createTemporaryWritableFile(".mp4");
    }

    private File createTemporaryWritableFile(String suffix) {
        String filename = UUID.randomUUID().toString();
        File image;

        try {
            image = File.createTempFile(filename, suffix, externalFilesDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return image;
    }

    private void grantUriPermissions(Intent intent, Uri imageUri) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> compatibleActivities =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo info : compatibleActivities) {
            activity.grantUriPermission(
                    info.activityInfo.packageName,
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted =
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                if (permissionGranted) {
                    launchPickImageFromGalleryIntent();
                }
                break;
            case REQUEST_EXTERNAL_VIDEO_STORAGE_PERMISSION:
                if (permissionGranted) {
                    launchPickVideoFromGalleryIntent();
                }
                break;
            case REQUEST_CAMERA_IMAGE_PERMISSION:
                if (permissionGranted) {
                    launchTakeImageWithCameraIntent();
                }
                break;
            case REQUEST_CAMERA_VIDEO_PERMISSION:
                if (permissionGranted) {
                    launchTakeVideoWithCameraIntent();
                }
                break;
            default:
                return false;
        }

        if (!permissionGranted) {
            switch (requestCode) {
                case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                case REQUEST_EXTERNAL_VIDEO_STORAGE_PERMISSION:
                    finishWithError("photo_access_denied", "The user did not allow photo access.");
                    break;
                case REQUEST_CAMERA_IMAGE_PERMISSION:
                case REQUEST_CAMERA_VIDEO_PERMISSION:
                    finishWithError("camera_access_denied", "The user did not allow camera access.");
                    break;
            }
        }

        return true;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY
                    || requestCode == REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA) {
                PictureHandleTask pictureHandleTask = new PictureHandleTask(requestCode, resultCode, data);
                pictureHandleTask.execute();
                return true;
            }
        } else if (pendingResult != null) {
            pendingResult.notImplemented();
        }
        return false;
    }

    private void handleChooseImageResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String path = fileUtils.getPathFromUri(activity, data.getData());
            handleImageResult(path, false);
            return;
        }

        // User cancelled choosing a picture.
        finishWithSuccess(null);
    }

    private void handleChooseVideoResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String path = fileUtils.getPathFromUri(activity, data.getData());
            handleVideoResult(path);
            return;
        }

        // User cancelled choosing a picture.
        finishWithSuccess(null);
    }

    private void handleCaptureImageResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            fileUriResolver.getFullImagePath(
                    pendingCameraMediaUri != null
                            ? pendingCameraMediaUri
                            : Uri.parse(ImagePickerCache.retrievePendingCameraMediaUriPath()),
                    new OnPathReadyListener() {
                        @Override
                        public void onPathReady(String path) {
                            //1 解决三星手机拍照旋转的问题 这里已经质量压缩到原来的十分之一了
//                            String filePath = PhotoBitmapUtils.amendRotatePhoto(path, activity);
                            handleImageResult(path, true);

                        }
                    });
            return;
        }

        // User cancelled taking a picture.
        finishWithSuccess(null);
    }

    private void handleCaptureVideoResult(int resultCode) {
        if (resultCode == RESULT_OK) {
            fileUriResolver.getFullImagePath(
                    pendingCameraMediaUri != null
                            ? pendingCameraMediaUri
                            : Uri.parse(ImagePickerCache.retrievePendingCameraMediaUriPath()),
                    new OnPathReadyListener() {
                        @Override
                        public void onPathReady(String path) {
                            handleVideoResult(path);
                        }
                    });
            return;
        }

        // User cancelled taking a picture.
        finishWithSuccess(null);
    }

    private void handleImageResult(String path, boolean shouldDeleteOriginalIfScaled) {
        if (methodCall != null) {
            Double maxWidth = methodCall.argument("maxWidth");
            Double maxHeight = methodCall.argument("maxHeight");
            Double compressSize = methodCall.argument("compressSize");
            //2 裁剪尺寸
            Bitmap bitmap = imageResizer.resizedBitmap(activity, path, maxWidth, maxHeight);
            if (bitmap == null) {
                if (pendingResult != null) {
                    pendingResult.notImplemented();
                }
                return;
            }
            //3 压缩大小
            String finalImagePath = PhotoBitmapUtils.getCompressPhotoUrl(externalFilesDirectory, bitmap, path, compressSize);
            finishWithSuccess(finalImagePath);
            //delete original file if scaled
            if (!finalImagePath.equals(path) && shouldDeleteOriginalIfScaled) {
                new File(path).delete();
            }
        } else {
            finishWithSuccess(path);
        }
    }


    ProgressDialog dialog;

    //异步任务
    public class PictureHandleTask extends AsyncTask {

        private int requestCode;
        private int resultCode;
        private Intent data;

        public PictureHandleTask(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
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
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY:
                    handleChooseImageResult(resultCode, data);
                    break;
                case REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA:
                    handleCaptureImageResult(resultCode);
                    break;
                case REQUEST_CODE_CHOOSE_VIDEO_FROM_GALLERY:
                    handleChooseVideoResult(resultCode, data);
                    break;
                case REQUEST_CODE_TAKE_VIDEO_WITH_CAMERA:
                    handleCaptureVideoResult(resultCode);
                    break;
                default:
                    return false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.dismiss();
        }

    }

    private void handleVideoResult(String path) {
        finishWithSuccess(path);
    }

    private boolean setPendingMethodCallAndResult(
            MethodCall methodCall, MethodChannel.Result result) {
//        if (pendingResult != null) {
//            return false;
//        }

        this.methodCall = methodCall;
        pendingResult = result;

        // Clean up cache if a new image picker is launched.
        ImagePickerCache.clear();

        return true;
    }

    private void finishWithSuccess(String imagePath) {
        if (pendingResult == null) {
            ImagePickerCache.saveResult(imagePath, null, null);
            return;
        }
        pendingResult.success(imagePath);
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
}

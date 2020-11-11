package com.zbar.lib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;

import aj.flutter.scan.R;
import anjiplus.aj.flutter.aj_flutter_scan.AjFlutterScanPlugin;
import anjiplus.aj.flutter.aj_flutter_scan.ImagePickerDelegate;

/**
 * 条形码/二维码扫描
 */
public class CaptureActivity extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private boolean isNeedCapture = false;
    private String TYPE = null;//类型 分从车辆扫描进来和从身份验证进来

    private static String scan_cancle = "SCAN_CANCLE"; //取消扫描
    private static ImagePickerDelegate delegate;


    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getIntent().getStringExtra("TYPE");
        setContentView(R.layout.activity_scanner);
        String scanTitle = getIntent().getStringExtra("scan_title");
        if (scanTitle != null) {
            TextView qrcode_notice = (TextView) findViewById(R.id.qrcode_notice);
            qrcode_notice.setText(scanTitle);
        }
        // 初始化CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        delegate = AjFlutterScanPlugin.getDelegate();
        findViewById(R.id.qr_back).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        onBackAction();
                    }
                });
        findViewById(R.id.qr_open_light).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        //Todo 打开闪光灯
                        light();
                    }
                });
        findViewById(R.id.tv_Gallery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo  申请权限后打开相册
                if (ContextCompat.checkSelfPermission(CaptureActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(CaptureActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }
                delegate.chooseImageFromGallery();
                finish();
            }
        });
        // 获得屏幕的宽高
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWith = displayMetrics.widthPixels;
        LayoutParams params = (LayoutParams) mCropLayout.getLayoutParams();
        params.width = screenWith * 2 / 3;
        params.height = screenWith * 2 / 3;
        mCropLayout.setLayoutParams(params);
        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
                0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBackAction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onBackAction() {
        Intent intent = new Intent();
        intent.putExtra("resultCode", scan_cancle);
        setResult(ImagePickerDelegate.REQUEST_CODE_TAKE_IMAGE_WITH_CAMERA_ERROR, intent);
        finish();
    }


    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 打开
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闭
            CameraManager.get().offLight();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
//        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra("resultCode", result);
        //TODO 如果需要连扫，如下操作
//        AjFlutterScanPlugin.result.success(result);
//        Message message = new Message();
//        message.what = msgRestartPreview;
//        handler.handleMessage(message);
        setResult(1, intent);
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            setNeedCapture(true);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
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

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionGranted =
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (requestCode == 1 && permissionGranted) {
            delegate.chooseImageFromGallery();
            finish();
        }
    }
}
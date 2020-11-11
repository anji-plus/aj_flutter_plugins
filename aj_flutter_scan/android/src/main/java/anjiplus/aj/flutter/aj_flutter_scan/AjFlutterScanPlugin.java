package anjiplus.aj.flutter.aj_flutter_scan;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.zbar.lib.CaptureActivity;

import java.io.File;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * AjFlutterScanPlugin
 */
public class AjFlutterScanPlugin implements MethodCallHandler {
    private static Registrar registrar;
    private static ImagePickerDelegate delegate;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        AjFlutterScanPlugin.registrar = registrar;

        final File externalFilesDirectory =
                registrar.activity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        final ExifDataCopier exifDataCopier = new ExifDataCopier();
        final ImageResizer imageResizer = new ImageResizer(externalFilesDirectory, exifDataCopier);
        delegate = new ImagePickerDelegate(registrar.activity(), externalFilesDirectory, imageResizer);
        registrar.addActivityResultListener(delegate);
        registrar.addRequestPermissionsResultListener(delegate);

        final MethodChannel channel = new MethodChannel(registrar.messenger(), "aj_flutter_scan");
        channel.setMethodCallHandler(new AjFlutterScanPlugin());
    }

    public static ImagePickerDelegate getDelegate() {
        return delegate;
    }

    // MethodChannel.Result wrapper that responds on the platform thread.
    private static class MethodResultWrapper implements MethodChannel.Result {
        private MethodChannel.Result methodResult;
        private Handler handler;

        MethodResultWrapper(MethodChannel.Result result) {
            methodResult = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object result) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.success(result);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            methodResult.notImplemented();
                        }
                    });
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        MethodChannel.Result resultWrapper = new MethodResultWrapper(result);
        if (call.method.equals("getBarCode")) {//默认相机，这里不改了
            delegate.chooseImageFromCamera(call, resultWrapper);
        } else if (call.method.equals("getBarCodeFromGallery")) {//从相册选择图片,可设置 maxWidth & maxHeight
            delegate.chooseImageFromGallery(call, resultWrapper);
        } else {
            result.notImplemented();
        }
    }
}

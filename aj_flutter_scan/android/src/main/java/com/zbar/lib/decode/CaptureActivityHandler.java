package com.zbar.lib.decode;

import android.os.Handler;
import android.os.Message;

import com.zbar.lib.CaptureActivity;
import com.zbar.lib.CommonMessage;
import com.zbar.lib.camera.CameraManager;

//import aj.flutter.scan.R;

import static com.zbar.lib.CommonMessage.msgAutoFocus;
import static com.zbar.lib.CommonMessage.msgDecodeFailed;
import static com.zbar.lib.CommonMessage.msgDecodeSucceeded;
import static com.zbar.lib.CommonMessage.msgRestartPreview;

public final class CaptureActivityHandler extends Handler {

	DecodeThread decodeThread = null;
	CaptureActivity activity = null;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case msgAutoFocus:
			if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, msgAutoFocus);
			}
			break;
		case msgRestartPreview:
			restartPreviewAndDecode();
			break;
		case msgDecodeSucceeded:
			state = State.SUCCESS;
			activity.handleDecode((String) message.obj);// 解析成功，回调
			break;

		case msgDecodeFailed:
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					CommonMessage.msgDecode);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.get().stopPreview();
		removeMessages(msgDecodeSucceeded);
		removeMessages(msgDecodeFailed);
		removeMessages(CommonMessage.msgDecode);
		removeMessages(msgAutoFocus);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
					CommonMessage.msgDecode);
			CameraManager.get().requestAutoFocus(this, msgAutoFocus);
		}
	}

}

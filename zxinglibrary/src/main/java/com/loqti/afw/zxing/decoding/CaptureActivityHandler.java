/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loqti.afw.zxing.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.loqti.afw.zxing.R;
import com.loqti.afw.zxing.ZXingConf;
import com.loqti.afw.zxing.activity.CaptureActivity;
import com.loqti.afw.zxing.camera.CameraManager;
import com.loqti.afw.zxing.view.ViewfinderResultPointCallback;

import java.util.Vector;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {

  private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final DecodeThread decodeThread;
  private State state;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public CaptureActivityHandler(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
      String characterSet) {
    this.activity = activity;
    decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
        new ViewfinderResultPointCallback(activity.getViewfinderView()));
    decodeThread.start();
    state = State.SUCCESS;
    // Start ourselves capturing previews and decoding.
    CameraManager.get().startPreview();
    restartPreviewAndDecode();
  }

  @Override
  public void handleMessage(Message message) {
    int what = message.what;
    if (what == R.id.zxing_auto_focus) {
      //Log.d(TAG, "Got auto-focus message");
      // When one auto focus pass finishes, start another. This is the closest thing to
      // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
      if (state == State.PREVIEW) {
        CameraManager.get().requestAutoFocus(this, R.id.zxing_auto_focus);
      }
    }else if (what ==  R.id.zxing_restart_preview) {
      if (ZXingConf.DEBUG) {
        Log.d(TAG, "Got restart preview message");
      }
      restartPreviewAndDecode();
    }else if (what == R.id.zxing_decode_succeeded) {
      if (ZXingConf.DEBUG) {
        Log.d(TAG, "Got decode succeeded message");
      }
      state = State.SUCCESS;
      Bundle bundle = message.getData();

      /***********************************************************************/
      Bitmap barcode = bundle == null ? null :
              (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);//���ñ����߳�

      activity.handleDecode((Result) message.obj, barcode);//���ؽ��
      /***********************************************************************/
    }else if (what == R.id.zxing_decode_failed) {
      // We're decoding as fast as possible, so when one decode fails, start another.
      state = State.PREVIEW;
      CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.zxing_decode);
    }else if (what == R.id.zxing_return_scan_result) {
      if (ZXingConf.DEBUG) {
        Log.d(TAG, "Got return scan result message");
      }
      activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
      activity.finish();
    }else if (what == R.id.zxing_launch_product_query) {
      if (ZXingConf.DEBUG) {
        Log.d(TAG, "Got product query message");
      }
      String url = (String) message.obj;
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      //TODO API 11+ FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET will bad
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK );
      activity.startActivity(intent);
    }
  }

  public void quitSynchronously() {
    state = State.DONE;
    CameraManager.get().stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.zxing_quit);
    quit.sendToTarget();
    try {
      decodeThread.join();
    } catch (InterruptedException e) {
      if (ZXingConf.DEBUG) {
        e.printStackTrace();
      }
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.zxing_decode_succeeded);
    removeMessages(R.id.zxing_decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
      CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.zxing_decode);
      CameraManager.get().requestAutoFocus(this, R.id.zxing_auto_focus);
      activity.drawViewfinder();
    }
  }
}

package com.jule.sinlov.scancode.view.create;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.jule.sinlov.scancode.R;
import com.jule.sinlov.scancode.application.ScanCodeApplication;
import com.loqti.afw.base.codewidget.ToastBuilder;
import com.loqti.afw.lifecycle.BaseActivity;
import com.loqti.afw.utils.InputMethod;
import com.loqti.afw.zxing.encoding.QRBitmapBuilder;

import java.lang.ref.WeakReference;

public class QRCodeCreateActivity extends BaseActivity implements View.OnClickListener {

    private EditText etInput;
    private Button btnSubmit;
    private ImageView imgQRResult;
    private TextView tvResult;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_act_create_submit:
                String input = etInput.getText().toString().trim();
                showQRBitmap(input);
                break;
            default:
                break;
        }
    }

    private void showQRBitmap(String input) {
        if (checkInput(input)) {
            InputMethod.closeInputPan(this);
            try {
//                Bitmap qrMap = QRBitmapBuilder.createQRBitMap(input, 800, 800);
//                EncodingHandler.createQRCode(input, 800);
                Bitmap qrMap = QRBitmapBuilder.createQRBitMapWithImage(input,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                if (null != qrMap) {
                    imgQRResult.setImageBitmap(qrMap);
                    tvResult.setText(input);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            ToastBuilder.make(ScanCodeApplication.getContext(), R.string.toast_msg_error_input, ToastBuilder.MIDDLE_TOAST);
        }
    }

    private boolean checkInput(String inputStr) {
        return !inputStr.equals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_create);
        initData();
        initView();
    }

    private void initView() {
        this.etInput = (EditText) findViewById(R.id.et_act_create_input);
        this.imgQRResult = (ImageView) findViewById(R.id.img_act_create_result);
        this.tvResult = (TextView) findViewById(R.id.tv_act_create_result);
        this.btnSubmit = (Button) findViewById(R.id.btn_act_create_submit);
        btnSubmit.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void doAfterHandleMessageSend(WeakReference<BaseActivity> weakReference, int i, Object o) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrcode_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

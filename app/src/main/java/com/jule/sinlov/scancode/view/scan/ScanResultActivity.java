package com.jule.sinlov.scancode.view.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jule.sinlov.scancode.R;
import com.loqti.afw.lifecycle.BaseActivity;
import com.loqti.afw.zxing.ZXingConf;
import com.loqti.afw.zxing.activity.CaptureActivity;

import java.lang.ref.WeakReference;

public class ScanResultActivity extends BaseActivity implements View.OnClickListener {
    
    private Button btnOpenScanCamera;
    private TextView tvScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        initData();
        initView();
    }

    private void initView() {
        this.tvScanResult = (TextView) findViewById(R.id.tv_act_scan_result_show);
        this.btnOpenScanCamera = (Button) findViewById(R.id.btn_act_scan_result_open_camera);
        btnOpenScanCamera.setOnClickListener(this);
    }

    private void initData() {
    }

    @Override
    public void doAfterHandleMessageSend(WeakReference<BaseActivity> weakReference, int i, Object o) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_result, menu);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_act_scan_result_open_camera:
                Intent scanIntent = new Intent(ScanResultActivity.this, CaptureActivity.class);
                startActivityForResult(scanIntent, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(ZXingConf.KEY_SCAN_RESULT);
            tvScanResult.setText(scanResult);
        }
    }
}

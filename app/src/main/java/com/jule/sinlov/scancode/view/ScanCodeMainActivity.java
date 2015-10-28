package com.jule.sinlov.scancode.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jule.sinlov.scancode.R;
import com.jule.sinlov.scancode.application.ScanCodeApplication;
import com.jule.sinlov.scancode.view.create.QRCodeCreateActivity;
import com.jule.sinlov.scancode.view.pub.DoubleClick2QuitApp;
import com.jule.sinlov.scancode.view.scan.ScanResultActivity;
import com.loqti.afw.lifecycle.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ScanCodeMainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ArrayList<String> debugItems;
    private ListView lvDemoList;
    private ArrayAdapter<String> stringArrayAdapter;
    private DoubleClick2QuitApp doubleClick2QuitApp;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(ScanCodeMainActivity.this, QRCodeCreateActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case 1:
                startActivity(new Intent(ScanCodeMainActivity.this, ScanResultActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            default:
                break;
        }
    }

    @Override
    public void doAfterHandleMessageSend(WeakReference<BaseActivity> weakReference, int i, Object o) {

    }

    private void initData() {
        debugItems = new ArrayList<String>();
        debugItems.add("Create Code");
        debugItems.add("Scan Code");
    }

    private void initView() {
        doubleClick2QuitApp = new DoubleClick2QuitApp(ScanCodeApplication.getContext());
        this.lvDemoList = (ListView) findViewById(R.id.lv_act_main_code_info);
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, debugItems);
        lvDemoList.setAdapter(stringArrayAdapter);
        lvDemoList.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_main);
        initData();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_code_main, menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        doubleClick2QuitApp.doubleClick2QuitApp(keyCode, event);
        return true;
    }
}

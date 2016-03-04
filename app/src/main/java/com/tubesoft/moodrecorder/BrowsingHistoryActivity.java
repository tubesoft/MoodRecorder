package com.tubesoft.moodrecorder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowsingHistoryActivity extends AppCompatActivity {

    private ArrayList<Map<String,Object>> rec = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listView = (ListView) findViewById(R.id.listView);

        try {
            loadCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // リストビューに渡すアダプタを生成
        adapter = new SimpleAdapter(
                this,
                rec,//ArrayList
                R.layout.list_history,//ListView内の1項目を定義したxml
                new String[]{"time", "samplingRate", "isTracked"},//mapのキー
                new int[]{R.id.time, R.id.samplingRate, R.id.isTracked});//item.xml内のid

        // アダプタをセット
        listView.setEmptyView(findViewById(R.id.emptyData));
        listView.setAdapter(adapter);

        //リスト項目が選択された時のイベントを追加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = position + "番目のアイテムがクリックされました";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        //リスト項目が長押しされた時のイベントを追加
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = position + "番目のアイテムが長押しされました";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void loadCsv() throws IOException {
        String path = "SavedRecord.txt";
        InputStream is = this.getAssets().open(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            Map data = new HashMap();
            System.out.println(line);
            data.put("time", line);
            line = in.readLine();
            data.put("samplingRate", line);
            line = in.readLine();
            data.put("isTracked", line);
            while (!in.readLine().equals("EOR")) {
            }
            rec.add(data);
        }
        in.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browsing_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_chosen:
                String msg = "選択削除押下！";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


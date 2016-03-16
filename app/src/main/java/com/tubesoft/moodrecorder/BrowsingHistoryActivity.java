package com.tubesoft.moodrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowsingHistoryActivity extends AppCompatActivity {

    private ArrayList<Map<String,Object>> rec = new ArrayList<>();
    private List<HistoryItems> list;
    private HistoryListAdapter adapter;
//    private SimpleAdapter adapter;
    private ListView listView;
    private boolean isBtnDeleteSelected = false;
    private MenuItem dltChn;
    private MenuItem dltCfm;
    private String path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ツールバーに戻るボタンを設置
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        path = getString(R.string.record_path);
        //リストビューを設定
        listView = (ListView) findViewById(R.id.list);
        try {
            loadCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new HistoryListAdapter(this,list);
        listView.setEmptyView((TextView)findViewById(R.id.empty));
        listView.setAdapter(adapter);


        //リスト項目が選択された時のイベントを追加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isBtnDeleteSelected){
                    CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox);
                    cb.setChecked(!cb.isChecked());
                } else {
//                    String msg = position + "番目のアイテムがクリックされました";
//                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    //結果画面に移動
                    Intent intentResult = new Intent(view.getContext(), ResultActivity.class);
                    intentResult.putExtra("is_from_history", true);
                    intentResult.putExtra("list_id", position);
                    intentResult.putExtra("list_size", list.size());
                    startActivity(intentResult);
                }
            }
        });

//        //リスト項目が長押しされた時のイベントを追加
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String msg = position + "番目のアイテムが長押しされました";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
    }

    //assetsフォルダ内のtxtファイルを読み込むメソッド
    private void loadCsv() throws IOException {
        InputStream is = this.getAssets().open(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        list = new ArrayList<HistoryItems>();
        String line;
        while ((line = in.readLine()) != null) {
            HistoryItems items = new HistoryItems();
            items.setTime(line);
            line = in.readLine();
            items.setSamplingRate(line);
            line = in.readLine();
            if (line.equals("true")){
                items.setIsTracked(true);
            } else {
                items.setIsTracked(false);
            }
            while (!in.readLine().equals("EOR")) {
                continue;
            }
            items.setIsChecked(false);
            list.add(items);
        }
        in.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browsing_history, menu);
        dltChn = menu.findItem(R.id.delete_chosen);
        dltCfm = menu.findItem(R.id.delete_confirm);
        dltCfm.setVisible(false);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
            switch (item.getItemId()) {
                case R.id.delete_chosen:
                    if (isBtnDeleteSelected) {
                        adapter.setCbVisibility(false);
                        adapter.notifyDataSetChanged();

                        isBtnDeleteSelected = false;
                        dltCfm.setVisible(false);
                        dltChn.setIcon(R.drawable.ic_action_edit_w);
                        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    } else {
                        adapter.setCbVisibility(true);
                        adapter.notifyDataSetChanged();

                        isBtnDeleteSelected = true;
                        dltCfm.setVisible(true);
                        dltChn.setIcon(R.drawable.ic_action_edit);
                        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    }
                    return true;
                case R.id.delete_confirm:
                    SparseBooleanArray checked = listView.getCheckedItemPositions();
                    List listDeleted = new ArrayList();
                    for (int i=0; i<checked.size(); i++){
                        if (checked.valueAt(i)){
                            listDeleted.add(checked.keyAt(i));
                        }
                    }

                    //削除メソッドを実装したが、後で試してみる。
//                    try {
//                        deleteData(listDeleted);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    //リスト確認用
                    StringBuffer sb = new StringBuffer();
                    for (int i=0; i<listDeleted.size(); i++){
                        sb.append(listDeleted.get(i));
                        sb.append("、");
                    }
                    sb.append("が削除されそうだよ");
                    Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();

                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
    }

    private void deleteData(List list) throws IOException{
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8")));
        InputStream is = this.getAssets().open(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line = "";
        // 削除すべきレコードを読み飛ばしてファイルに書き込み。
        for (int i=0; i< list.size(); i++) {
            if (list.contains(i)){
                while(!in.readLine().equals("EOR")){
                    continue;
                }
                continue;
            } else {
                while(!in.readLine().equals("EOR")){
                    line = in.readLine();
                    out.println(line);
                }
                out.println("EOR");
            }
        }
    }
}


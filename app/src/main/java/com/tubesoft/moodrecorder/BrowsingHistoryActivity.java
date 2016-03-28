package com.tubesoft.moodrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BrowsingHistoryActivity extends AppCompatActivity {

    private ArrayList<Map<String,Object>> rec = new ArrayList<>();
    private List<HistoryItems> listItems;
    private HistoryListAdapter adapter;
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
        //保存ファイルがあるか確認
        try {
            checkFileExist();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保存ファイル読み込み
        try {
            loadCsv();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter = new HistoryListAdapter(this, listItems);
        listView.setEmptyView((TextView)findViewById(R.id.empty));
        listView.setAdapter(adapter);


        //リスト項目が選択された時のイベントを追加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isBtnDeleteSelected){
                    CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox);
                    cb.setChecked(!cb.isChecked());
                } else {
                    //結果画面に移動
                    Intent intentResult = new Intent(view.getContext(), ResultActivity.class);
                    intentResult.putExtra("is_from_history", true);
                    intentResult.putExtra("list_id", position);
                    intentResult.putExtra("list_size", listItems.size());
                    startActivity(intentResult);
                }
            }
        });
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
                    final List<Integer> listDeleted = new ArrayList<>();
                    for (int i=0; i<checked.size(); i++){
                        if (checked.valueAt(i)){
                            listDeleted.add(checked.keyAt(i));
                        }
                    }

                    //削除。
                    int deleteSize = listDeleted.size();
                    //何も選んでないときに押したときの警告
                    if (deleteSize==0){
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.warning_message))
                                .setPositiveButton("OK", null)
                                .show();
                    }else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(getString(R.string.delete_confirmation1));
                        sb.append(String.valueOf(deleteSize));
                        sb.append(getString(R.string.delete_confirmation2));
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.warning))
                                .setMessage(sb.toString())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // OK button pressed
                                        System.out.println("OK押下");
                                        try {
                                            deleteData(listDeleted);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
    }

    //txtファイルを読み込むメソッド
    private void loadCsv() throws IOException, ParseException {
        InputStream is = this.openFileInput(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        listItems = new ArrayList<HistoryItems>();
        String line;
        while ((line = in.readLine()) != null) {
            HistoryItems items = new HistoryItems();
            //保存時刻の読み込み
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
            Date date = sdf1.parse(line);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN);
            String savedDate = sdf2.format(date);
            items.setTime(savedDate);
            line = in.readLine();
            //サンプリングレート読み込み
            StringBuilder sb = new StringBuilder();
            sb.append(line);
            sb.append(" Hz");
            items.setSamplingRate(sb.toString());
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
            listItems.add(items);
        }
        in.close();
    }

    private void deleteData(List dList) throws IOException, ParseException {
        InputStream is = this.openFileInput(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        List<String> writeList = new ArrayList<String>();
        // 削除すべきレコードを読み飛ばしてリストに格納、ファイルに書き込み。
        for (int i=0; i< listItems.size(); i++) {
            if ((line = in.readLine()) == null) {
                break;
            }
            if (dList.contains(i)){
                while(!line.equals("EOR")){
                    line = in.readLine();
                }
            } else {
                while(!line.equals("EOR")){
                    writeList.add(line);
                    line = in.readLine();
                }
                writeList.add("EOR");
            }
        }
        in.close();
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(path, MODE_PRIVATE),"UTF-8")));
        for (int i=0; i<writeList.size(); i++){
            out.println(writeList.get(i));
        }
        out.close();
        loadCsv();
        adapter.clear();
        adapter.addAll(listItems);
        adapter.notifyDataSetChanged();
    }

    public void checkFileExist() throws IOException {
        //とりあえずファイルがないときはassetsのテストデータを読み込むようにしておく。そのうち空ファイル作成に変更。
        if (this.openFileInput(path) == null) {

            //空ファイルの作成
//            File newFile = new File(path);
//            newFile.createNewFile();

            InputStream is = this.getAssets().open(path);
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            List<String> writeList = new ArrayList<String>();
            while((line = in.readLine()) != null){
                writeList.add(line);
            }
            in.close();
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            openFileOutput(path, MODE_PRIVATE), "UTF-8")));
            for (int i=0; i<writeList.size(); i++){
                out.println(writeList.get(i));
            }
            out.close();
        }

        //テストデータを元に戻したいときは、以下のコメントアウトを解除（assetsにおいてあるファイルから読み込む）
//        InputStream is = this.getAssets().open(path);
//        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//        String line;
//        List<String> writeList = new ArrayList<String>();
//        while((line = in.readLine()) != null){
//            writeList.add(line);
//        }
//        in.close();
//        PrintWriter out = new PrintWriter(
//                new BufferedWriter(new OutputStreamWriter(
//                        openFileOutput(path, MODE_PRIVATE), "UTF-8")));
//        for (int i=0; i<writeList.size(); i++){
//            out.println(writeList.get(i));
//        }
//        out.close();

    }

}


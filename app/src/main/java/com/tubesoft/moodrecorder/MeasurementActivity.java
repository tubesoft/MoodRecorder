package com.tubesoft.moodrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeasurementActivity extends AppCompatActivity implements View.OnTouchListener{

//    private ImageView img;
    private float xPos;
    private float yPos;
    private TextView positionText;
    protected Timer countdownTimer;
    protected CountdownTimerTask countdownTimerTask;
    private TextView countText;					//テキストビュー
    private int count = 3;						//カウント
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
    private SharedPreferences pref;     //設定クラス
    private TextView timeText;
    private int samplingRate;
    private  boolean isTracked;
    private int measureSec;     //測定時間の秒（メイン画面で設定したもの）
    private int measureMin;     //測定時間の分
    private int measureParseMSec;     //測定時間のミリ秒変換
    private int countMeasureMSec;   //測定時間のカウント
    private int secCounter; //一秒間の間に通って回数をカウント
    private List listPositions;
    private List listTimestamps;
    protected Timer measurementTimer;
    protected MeasurementTimerTask measurementTimerTask;
    private String path; //データ保存先のパス
    private DecimalFormat df;
    private int countRecord; //保存しているレコード数
    //軌跡の描画関連
    private Path trackPath;
    private boolean canTrack;
    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        path = getString(R.string.record_path);
        listPositions = new ArrayList();
        listTimestamps = new ArrayList();

        //設定からサンプリングレートを取得
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        samplingRate = Integer.parseInt(pref.getString("pref_sampling_rate", "20"));
        System.out.println(samplingRate);
        //設定から軌跡取得の有無を取得
        isTracked = pref.getBoolean("pref_is_tracked", false);

        //メインアクティビティで設定した時間を取得
        MeasurementTime time = (MeasurementTime)getIntent().getSerializableExtra("measurement_time");
        measureSec = time.getTime(MeasurementTime.times.SEC);
        measureMin = time.getTime(MeasurementTime.times.MIN);
        timeText = (TextView)findViewById(R.id.textViewTime);
        df = new DecimalFormat("00");
        setTimeStr();

        //座標表示テキスト
        positionText = (TextView)findViewById(R.id.position);

        //軌跡の描画準備
        drawView = (DrawView)findViewById(R.id.drewView);
        canTrack = false;

        //カウントダウンタイマー
        countText = (TextView)findViewById(R.id.textCountdown);
        countText.setText(String.valueOf(count));
        this.countdownTimer = new Timer();
        this.countdownTimerTask = new CountdownTimerTask();
        //schedule(task,time, period)taskオブジェクトのrunメソッドを時刻timeを開始時点としてperiod（ミリ秒）間隔で実行する
        this.countdownTimer.scheduleAtFixedRate(countdownTimerTask, 1000, 1000);

        //測定タイマー
        measureParseMSec = (measureSec + measureMin*60)*1000;
        countMeasureMSec = 0;
        secCounter = 0;
        measurementTimer = new Timer();
        this.measurementTimerTask = new MeasurementTimerTask();
        drawView.setOnTouchListener(this);
        listPositions = new ArrayList();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xPos = event.getX();
                yPos = event.getY();
                positionText.setText("X: " + Float.toString(xPos) + " Y: " + Float.toString(yPos));
                trackPath = new Path();
                trackPath.moveTo(xPos,yPos);
                drawView.pathList.add(trackPath);
                break;
            case MotionEvent.ACTION_MOVE:
                xPos = event.getX();
                yPos = event.getY();
                positionText.setText("X: " + Float.toString(xPos) + " Y: " + Float.toString(yPos));
                trackPath.lineTo(xPos,yPos);
                break;
            case MotionEvent.ACTION_UP:
                trackPath.lineTo(xPos,yPos);
                break;
        }
        if (canTrack){
            drawView.invalidate();
        }
            return true;
    }

    //測定前のカウントダウン
    //タイマータスク派生クラス
    //run()に定周期で処理したい内容を記述
    public class CountdownTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post( new Runnable() {
                public void run() {

                    if (count > 0) {
                        //実行間隔分を加算処理
                        count -= 1;
                        //画面にカウントを表示
                        countText.setText(String.valueOf(count));
                    } else {
                        countText.setVisibility(View.GONE);
                        countdownTimer.cancel();
                        //測定タイマーを起動（更新間隔は1秒をレートで割る）
                        measurementTimer.scheduleAtFixedRate(measurementTimerTask,0 , (1000/samplingRate));
                        //ここで軌跡の表示をする場合オンにする
                        if (isTracked){
                            canTrack = true;
                        }
                    }
                }
            });
        }
    }

    //測定時の処理
    public class MeasurementTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post( new Runnable() {
                public void run() {
                    //はじめに設定した時間内で処理を終わらせるようにする
                    //以下で測定した座標および時刻をリストに格納する
                    long currentTime = System.currentTimeMillis();
                    float position[] = {xPos,yPos};
                    if (countMeasureMSec < measureParseMSec) {
                        listTimestamps.add(currentTime);
                        listPositions.add(position);
                    } else {
                        measurementTimer.cancel();
                        showSaveDialogue();
                    }
                    countMeasureMSec = countMeasureMSec + 1000/samplingRate;

                    //残り時間表示の処理
                    secCounter++;
                    if (secCounter>=samplingRate) {
                        measureSec--;
                        secCounter = 0;
                        if (measureSec<0) {
                            measureMin--;
                            measureSec = 59;
                        }
                        setTimeStr();
                    }
                }
            });
        }

    }

    private void setTimeStr() {
        StringBuffer sb = new StringBuffer();
        sb.append(df.format(measureMin));
        sb.append(":");
        sb.append(df.format(measureSec));
        timeText.setText(sb.toString());
    }

    //↓↓測定が終わったときの処理：リストを保存ファイルに書き込む＋結果画面に遷移（履歴から来ていないフラグを渡す）

    //保存の確認ダイアログ
    private void showSaveDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm))
                .setMessage(getString(R.string.question_save))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            finishingMeasurement(listPositions, listTimestamps);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    //測定記録の保存
    private void finishingMeasurement(List listPos, List listTime) throws IOException {
        //ファイルがないときは空のものを作成
        File file = new File((new StringBuffer()).append(getFilesDir()).append("/").append(path).toString());
        if (!file.exists()) {
            file.createNewFile();
        }
        //これまでの記録をリストに読み込む
        List recordList = new ArrayList<HistoryItems>();
        InputStream is = this.openFileInput(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        countRecord = 0;
        while ((line = in.readLine()) != null) {
            recordList.add(line);
            if (line.equals("EOR")) {
                countRecord++;
            }
        }
        in.close();
        //最後に今の結果をだす
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDateStr = sdf.format(currentDate);
        String samplingRateStr = Integer.toString(samplingRate);
        String isTrackedStr = Boolean.toString(isTracked);
        recordList.add(currentDateStr);
        recordList.add(samplingRateStr);
        recordList.add(isTrackedStr);
        float height = drawView.getHeight();
        float width = drawView.getWidth();
        for (int i = 0; i < listPos.size(); i++) {
            float[] coordinate = (float[]) listPos.get(i);
            Date date = new Date((long)listTime.get(i));
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SSS");
            StringBuffer sb = new StringBuffer();
            sb.append(sdf2.format(date));
            sb.append(",");
            sb.append(Float.toString((coordinate[0]*2-width)/width));
            sb.append(",");
            sb.append(Float.toString((coordinate[1]*2-height)/height));
            recordList.add(sb.toString());
        }
        recordList.add("EOR");

        PrintWriter out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(
                        openFileOutput(path, MODE_PRIVATE), "UTF-8")));
        for (int j = 0; j < recordList.size(); j++) {
            System.out.println(recordList.get(j));
            out.println(recordList.get(j));
        }
        out.close();
        //結果画面に移動
        Intent intentResult = new Intent(this, ResultActivity.class);
        intentResult.putExtra("is_from_history", false);
        intentResult.putExtra("list_id", countRecord+1);
        startActivity(intentResult);

    }
}

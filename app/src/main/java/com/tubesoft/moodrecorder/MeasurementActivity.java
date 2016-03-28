package com.tubesoft.moodrecorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MeasurementActivity extends AppCompatActivity implements View.OnTouchListener{

    private ImageView img;
    private float xPos;
    private float yPos;
    private TextView positionText;
    protected Timer countdownTimer;
    protected CountdownTimerTask countdownTimerTask;
    private TextView countText;					//テキストビュー
    private int count = 5;						//カウント
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
    private SharedPreferences pref;     //設定クラス
    private int samplingRate;
    private  boolean isTracked;
    private int measureSec;     //測定時間の秒（メイン画面で設定したもの）
    private int measureMin;     //測定時間の分
    protected Timer measurementTimer;
    protected MeasurementTimerTask measurementTimerTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        TextView timeText = (TextView)findViewById(R.id.textViewTime);
        timeText.setText(measureMin + "分" + measureSec + "秒");

        //座標取得ビューを取得
        positionText = (TextView)findViewById(R.id.position);
        img = (ImageView)findViewById(R.id.imageView);
        img.setOnTouchListener(this);

        //カウントダウンタイマー
        countText = (TextView)findViewById(R.id.textCountdown);
        countText.setText(String.valueOf(count));
        this.countdownTimer = new Timer();
        this.countdownTimerTask = new CountdownTimerTask();
        //schedule(task,time, period)taskオブジェクトのrunメソッドを時刻timeを開始時点としてperiod（ミリ秒）間隔で実行する
        this.countdownTimer.schedule(countdownTimerTask, 1000, 1000);

        //測定タイマー
        this.measurementTimer = new Timer();
        this.measurementTimerTask = new MeasurementTimerTask();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                xPos = event.getX();
                yPos = event.getY();
                System.out.println(Float.toString(xPos));
                System.out.println(Float.toString(yPos));
                positionText.setText("X: " + Float.toString(xPos) + " Y: " + Float.toString(yPos));

                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                xPos = event.getX();
                yPos = event.getY();
                System.out.println(Float.toString(xPos));
                System.out.println(Float.toString(yPos));
                positionText.setText("X: " + Float.toString(xPos) + " Y: " + Float.toString(yPos));

                return false;

            }

            return false;

    }


    /**
     * タイマータスク派生クラス
     * run()に定周期で処理したい内容を記述
     *
     */
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
                        //測定タイマーを起動（更新間隔は1秒をレートで割る）※いまのとこコメントアウトしときます。
//                        measurementTimer.schedule(measurementTimerTask,0 , (1000/samplingRate));
                        //ここで軌跡の表示をする場合オンにする

                    }

                }
            });
        }
    }

    public class MeasurementTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post( new Runnable() {
                public void run() {
                    //はじめに設定した時間内で処理を終わらせるようにする
                    //以下で測定した座標および時刻をリストに格納する

                }
            });
        }
    }

//※測定が終わったときの処理：リストを保存ファイルに書き込む＋結果画面に遷移（履歴から来ていないフラグを渡す）
}

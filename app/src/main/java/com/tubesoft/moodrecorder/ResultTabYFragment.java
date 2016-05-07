package com.tubesoft.moodrecorder;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by inotazo on 16/03/15.
 */
public class ResultTabYFragment extends Fragment {

    String date;
    String samplingRate;
    boolean isTracked;
    LineData lineData;
    LineDataSet dataSet;
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_tab_y, container, false);
        title = getContext().getString(R.string.y_value);
        LineChart lineChart = (LineChart) view.findViewById(R.id.y_chart);
//        setChartProperty(lineChart);
        Intent intent = getActivity().getIntent();
        boolean isFromHistory = intent.getBooleanExtra("is_from_history", false);
        //履歴からのときは格納したリストid、測定からのときは末尾のidを取り出す。
        int listId = -1;
        if (isFromHistory){
            listId = intent.getIntExtra("list_id", -1);
        } else {
            try {
                listId = countListSize() - 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            loadCsv(listId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineChart.setData(lineData); // set the data and list of labels into chart
        lineChart.setDescription(title);

        return view;
    }

    //txtファイルを読み込むメソッド
    private void loadCsv(int id) throws IOException {
        String path = getContext().getString(R.string.record_path);
        InputStream is = getContext().openFileInput(path);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String line;
        for (int i=0; i<id; i++){
            while (!in.readLine().equals("EOR,,")) {
                continue;
            }
        }
        line = in.readLine();
        date = line.substring(0,line.indexOf(","));
        line = in.readLine();
        samplingRate = line.substring(0,line.indexOf(","));
        line = in.readLine();
        if (line.equals("true,,")) {
            isTracked = true;
        } else {
            isTracked = false;
        }

        List listEntry = new ArrayList();
        List listTime = new ArrayList();
        int cnt = 0;
        line = in.readLine();
        while (!line.equals("EOR,,")) {
            String[] items = line.split(",");
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//            String timeStr = items[0];
//            ParsePosition pos = new ParsePosition(0);
//            Date time = dateFormat.parse(timeStr,pos);

            //お試し
//            System.out.println("Y:"+items[1]);
//            System.out.println("Time:" + items[0]);
//            float x = Float.parseFloat(items[1]);
            float y = Float.parseFloat(items[2]);
            listEntry.add(new Entry(y,cnt));
            listTime.add(items[0]);
//            System.out.println(cnt);
            //お試し終わり

            cnt++;
            line = in.readLine();
        }
        in.close();
        dataSet = new LineDataSet(listEntry,title);
        lineData = new LineData(listTime,dataSet);
    }

    //グラフのフォーマットを定義するメソッド
    private void setChartProperty (LineChart mChart){
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);


        //  ラインの凡例の設定
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setLabelsToSkip(9);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(10.0f);
        leftAxis.setAxisMinValue(-10.0f);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private int countListSize() throws IOException {
        String path = getContext().getString(R.string.record_path);
        InputStream is = getContext().openFileInput(path);
        int size = 0;
        BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("EOR,,")){
                size++;
            }
        }
        return size;
    }

}

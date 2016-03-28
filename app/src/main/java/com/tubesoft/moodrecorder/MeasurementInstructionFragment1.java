package com.tubesoft.moodrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by inotazo on 2016/03/27.
 */
public class MeasurementInstructionFragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement_instruction1, container, false);
        Intent intent = getActivity().getIntent();

        //メインアクティビティから設定した時間を取得→たぶんこれはスタートボタン押下時にいれるのがいいかも。
        final MeasurementTime times = (MeasurementTime)intent.getSerializableExtra("measurement_time");

        Button btnStartMeasurement = (Button)view.findViewById(R.id.btnStartMeasurement);
        btnStartMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMeasurement = new Intent(getContext(), MeasurementActivity.class);
                intentMeasurement.putExtra("measurement_time", times);
                startActivity(intentMeasurement);
            }
        });
        return view;
    }


}

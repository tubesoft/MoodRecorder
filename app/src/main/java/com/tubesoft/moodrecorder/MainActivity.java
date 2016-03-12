package com.tubesoft.moodrecorder;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.NumberPicker;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    NumberPicker npSec;
    NumberPicker npMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        npSec = (NumberPicker) findViewById(R.id.numPickerSec);
        npMin = (NumberPicker) findViewById(R.id.numPickerMin);

        npSec.setMaxValue(59);
        npSec.setMinValue(1);
        npMin.setMaxValue(30);
        npMin.setMinValue(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mood_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.preferences:
                startActivity(new Intent(this, PreferencesAppActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnToOtameshi:
                startActivity(new Intent(this, OtameshiActivity.class));
                break;
            case R.id.btnMeasurement:
                //ナンバーピッカーの値を取得
                MeasurementTime time = new MeasurementTime(npMin.getValue(), npSec.getValue());
                Intent intentMeasurementInstruction = new Intent(this, MeasurementInstructionActivity.class);
                intentMeasurementInstruction.putExtra("measurement_time", time);
                startActivity(intentMeasurementInstruction);
                break;
            case R.id.btnHistory:
                startActivity(new Intent(this, BrowsingHistoryActivity.class));
                break;
            case R.id.btnDirect:
                System.out.println("PRESSED");
                startActivity(new Intent(this, MeasurementActivity.class));
        }
    }
}

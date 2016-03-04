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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NumberPicker npSec = (NumberPicker)findViewById(R.id.numPickerSec);
        NumberPicker npMin = (NumberPicker)findViewById(R.id.numPickerMin);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnToOtameshi:
                Intent intentOtameshi = new Intent(this, OtameshiActivity.class);
                startActivity(intentOtameshi);
                break;
            case R.id.btnMeasurement:
                Intent intentMeasurement = new Intent(this, MeasurementActivity.class);
                startActivity(intentMeasurement);
                break;
            case R.id.btnHistory:
                Intent intentHistory = new Intent(this, BrowsingHistoryActivity.class);
                startActivity(intentHistory);
                break;
            case R.id.btnPreference:
                Intent intentPreference = new Intent(this, PreferenceActivity.class);
                startActivity(intentPreference);
                break;
        }
    }
}

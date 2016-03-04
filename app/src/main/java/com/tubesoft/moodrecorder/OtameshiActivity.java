package com.tubesoft.moodrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tubesoft.moodrecorder.R;

public class OtameshiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otameshi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ツールバーに戻るボタンを設置
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnToMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }


}

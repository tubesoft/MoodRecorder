package com.tubesoft.moodrecorder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MeasurementActivity extends AppCompatActivity implements View.OnTouchListener{

    ImageView img;
    float xPos;
    float yPos;
    TextView positionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        positionText = (TextView)findViewById(R.id.position);
        img = (ImageView)findViewById(R.id.imageView);
        img.setOnTouchListener(this);
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
}

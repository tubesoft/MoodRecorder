package com.tubesoft.moodrecorder;

import java.io.Serializable;

/**
 * Created by inotazo on 16/03/04.
 */
public class MeasurementTime implements Serializable {

    int minute = 0;
    int second = 0;
    enum times {MIN, SEC}

    public MeasurementTime(int min, int sec) {
        minute = min;
        second = sec;
    }

    public int getTime(times t){
        switch(t){
            case MIN: return minute;
            case SEC: return second;
            default: return -1;
        }
    }
}

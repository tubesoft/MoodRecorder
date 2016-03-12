package com.tubesoft.moodrecorder;

/**
 * Created by inotazo on 16/03/11.
 */
public class HistoryItems {
    private String time;
    private String samplingRate;
    private boolean isTracked;
    private boolean isChecked;

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getSamplingRate() {
        return samplingRate;
    }
    public void setSamplingRate(String samplingRate) {
        this.samplingRate = samplingRate;
    }

    public boolean getIsTracked() {
        return isTracked;
    }
    public void setIsTracked(boolean isTracked) {
        this.isTracked = isTracked;
    }

    public boolean getIsChecked() {
        return isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


}

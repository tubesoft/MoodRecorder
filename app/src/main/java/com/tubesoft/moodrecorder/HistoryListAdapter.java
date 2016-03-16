package com.tubesoft.moodrecorder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by inotazo on 16/03/11.
 */
public class HistoryListAdapter extends ArrayAdapter<HistoryItems>{

    private LayoutInflater mInflater;
    private TextView mTime;
    private TextView mSamplingRate;
    private TextView mIsTracked;
    private CheckBox mIsChecked;
    private boolean isCbVisible = false;

    public HistoryListAdapter(Context context, List<HistoryItems> objects) {
        // 親のコンストラクタを呼び出す
        super(context, 0, objects);
        // インフレーターを取得する
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_history, parent, false);
        }
        final HistoryItems item = this.getItem(position);
        if(item != null){
            mTime = (TextView)convertView.findViewById(R.id.time);
            mTime.setText(item.getTime());
            mSamplingRate = (TextView)convertView.findViewById(R.id.samplingRate);
            mSamplingRate.setText(item.getSamplingRate());
            mIsTracked = (TextView)convertView.findViewById(R.id.isTracked);
            if (item.getIsTracked()){
                mIsTracked.setText(getContext().getText(R.string.with_track));
            } else {
                mIsTracked.setText(getContext().getText(R.string.without_track));
            }
            mIsChecked = (CheckBox)convertView.findViewById(R.id.checkBox);
            mIsChecked.setChecked(false);

            if (isCbVisible){
                mIsChecked.setVisibility(View.VISIBLE);
            } else {
                mIsChecked.setVisibility(View.INVISIBLE);
            }

        }
        return convertView;
    }

    public void setCbVisibility (Boolean visibility) {

        isCbVisible = visibility;

    }

}

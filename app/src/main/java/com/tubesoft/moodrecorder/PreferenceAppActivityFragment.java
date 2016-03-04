package com.tubesoft.moodrecorder;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by inotazo on 16/03/04.
 */

public class PreferenceAppActivityFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}

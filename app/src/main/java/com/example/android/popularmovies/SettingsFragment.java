package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

/**
 * Created by Jay on 12/12/2016.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


        if (!isOnline()){
            addPreferencesFromResource(R.xml.pref_general_offline);
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen prefscreen = getPreferenceScreen();

            int count = prefscreen.getPreferenceCount();
            for(int i=0 ; i<count ; i++){
                Preference p = prefscreen.getPreference(i);
                ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_sort_order_key));
                listPreference.setValue(getString(R.string.pref_favourite_value));
                if(!(p instanceof CheckBoxPreference)){
                    String value = sharedPreferences.getString(p.getKey(),getString(R.string.pref_favourite_value));
                    setPreferenceSummary(p,value);
                }
            }
        }else {
            addPreferencesFromResource(R.xml.pref_general);
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen prefscreen = getPreferenceScreen();

            int count = prefscreen.getPreferenceCount();
            for(int i=0 ; i<count ; i++){
                Preference p = prefscreen.getPreference(i);

                if(!(p instanceof CheckBoxPreference)){
                    String value = sharedPreferences.getString(p.getKey(),"");
                    setPreferenceSummary(p,value);
                }
            }
        }


    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference){

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex>=0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

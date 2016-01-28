package com.cac.viewer;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.telephony.TelephonyManager;
import android.view.View;

import com.cac.R;
import com.cac.sgc.MainActivity;
import com.cac.tools.MainComponentEdit;

import java.util.List;

/**
 * Created by miguel on 03/11/15.
 */
public class SettingFragment extends PreferenceFragment implements MainComponentEdit {

    public static final String TAG = "SettingFragment";
    private static SettingFragment ourInstance;
    private MainActivity context;

    private EditTextPreference editTextEmei;
    private EditTextPreference etpLastExecution;

    public static SettingFragment getInstance(MainActivity context){
        if(ourInstance == null) {
            ourInstance = new SettingFragment();
            ourInstance.context = context;
        }
        return  ourInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_sync);

        editTextEmei = (EditTextPreference) findPreference("EMEI");
        TelephonyManager telephonyManager = (TelephonyManager)ourInstance.context.getSystemService(Context.TELEPHONY_SERVICE);
        editTextEmei.setSummary(telephonyManager.getDeviceId());
        editTextEmei.setEnabled(false);

        updateLastExecutionSummary();
    }

    @Override
    public void onClickFloating(View view) {

    }

    @Override
    public void MainViewConfig(List<View> views) {
        for ( View view : views){
            switch (view.getId()){
                case R.id.btn_fab_right:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_fab_left:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.main_body_action_layout:
                    view.getLayoutParams().height = 0;
                    break;
            }
        }
    }

    @Override
    public String getTAG() {
        return "SettingFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.settings;
    }

    public void updateLastExecutionSummary(){
        etpLastExecution = (EditTextPreference) findPreference("etp_last_execution");
        etpLastExecution.setSummary(etpLastExecution.getText());
    }
}

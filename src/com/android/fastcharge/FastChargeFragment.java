/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.fastcharge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.android.fastcharge.R;
import com.android.fastcharge.utils.FileUtils;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;

public class FastChargeFragment extends SettingsBasePreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ListPreference mChargingModePreference;
    private FastChargeConfig mConfig;
    private boolean mInternalModeChange = false;

    private final BroadcastReceiver mServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mConfig.ACTION_FAST_CHARGE_SERVICE_CHANGED)) {
                if (mInternalModeChange) {
                        mInternalModeChange = false;
                        return;
                }

                if (mChargingModePreference == null) return;

                final String mode = intent.getStringExtra(mConfig.EXTRA_FAST_CHARGE_MODE);
                if (mode != null) {
                    mChargingModePreference.setValue(mode);
                    updateSummary(mode);
                }
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fastcharge_settings, rootKey);
        mConfig = FastChargeConfig.getInstance(getContext());
        mChargingModePreference = (ListPreference) findPreference(mConfig.FASTCHARGE_KEY);
        
        if (FileUtils.fileExists(mConfig.getFastChargePath())) {
            mChargingModePreference.setEnabled(true);
            mChargingModePreference.setOnPreferenceChangeListener(this);
            
            String currentMode = mConfig.getCurrentMode();
            mChargingModePreference.setValue(currentMode);
            updateSummary(currentMode);
        } else {
            mChargingModePreference.setSummary(R.string.wired_charging_mode_not_supported);
            mChargingModePreference.setEnabled(false);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(mConfig.ACTION_FAST_CHARGE_SERVICE_CHANGED);
        getContext().registerReceiver(mServiceStateReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentMode = mConfig.getCurrentMode();
        mChargingModePreference.setValue(currentMode);
        updateSummary(currentMode);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mConfig.FASTCHARGE_KEY.equals(preference.getKey())) {
            mInternalModeChange = true;
            Context mContext = getContext();

            String mode = (String) newValue;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            FileUtils.writeLine(mConfig.getFastChargePath(), mode);

            sharedPrefs.edit().putString(mConfig.FASTCHARGE_KEY, mode).commit();

            Intent intent = new Intent(mConfig.ACTION_FAST_CHARGE_SERVICE_CHANGED);
            intent.putExtra(mConfig.EXTRA_FAST_CHARGE_MODE, mode);
            intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);

            updateSummary(mode);
        }
        return true;
    }

    private void updateSummary(String mode) {
        if (mChargingModePreference == null) return;
        
        String summary;
        switch (mode) {
            case FastChargeConfig.MODE_SLOW:
                summary = getString(R.string.charging_mode_slow);
                break;
            case FastChargeConfig.MODE_FAST:
                summary = getString(R.string.charging_mode_fast);
                break;
            case FastChargeConfig.MODE_SUPER_FAST:
                summary = getString(R.string.charging_mode_super_fast);
                break;
            default:
                summary = getString(R.string.charging_mode_fast);
                break;
        }
        mChargingModePreference.setSummary(summary);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mServiceStateReceiver);
    }

}

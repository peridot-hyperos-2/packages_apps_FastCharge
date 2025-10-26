/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.fastcharge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;

import com.android.fastcharge.utils.FileUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final boolean DEBUG = false;
    private static final String TAG = "FastCharge";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG)
            Log.d(TAG, "Received boot completed intent");

        FastChargeConfig mConfig = FastChargeConfig.getInstance(context);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Restore saved charging mode, default to Fast mode (1)
        String chargingMode = sharedPrefs.getString(mConfig.FASTCHARGE_KEY, FastChargeConfig.MODE_FAST);
        
        if (DEBUG)
            Log.d(TAG, "Restoring charging mode: " + chargingMode);
        
        FileUtils.writeLine(mConfig.getFastChargePath(), chargingMode);
    }
}

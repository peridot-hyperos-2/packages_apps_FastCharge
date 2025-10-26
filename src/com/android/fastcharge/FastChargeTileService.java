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
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import com.android.fastcharge.R;
import com.android.fastcharge.utils.FileUtils;

public class FastChargeTileService extends TileService {

    private FastChargeConfig mConfig;

    private Intent mFastChargeIntent;

    private boolean mInternalModeChange;

    private final BroadcastReceiver mServiceStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mInternalModeChange) {
                mInternalModeChange = false;
                return;
            }
            updateUI();
        }
    };

    private void updateUI() {
        final Tile tile = getQsTile();
        String currentMode = mConfig.getCurrentMode();

        switch (currentMode) {
            case FastChargeConfig.MODE_SLOW:
                tile.setLabel(getString(R.string.charging_mode_slow));
                tile.setState(Tile.STATE_INACTIVE);
                break;
            case FastChargeConfig.MODE_FAST:
                tile.setLabel(getString(R.string.charging_mode_fast));
                tile.setState(Tile.STATE_ACTIVE);
                break;
            case FastChargeConfig.MODE_SUPER_FAST:
                tile.setLabel(getString(R.string.charging_mode_super_fast));
                tile.setState(Tile.STATE_ACTIVE);
                break;
            default:
                tile.setLabel(getString(R.string.charging_mode_fast));
                tile.setState(Tile.STATE_ACTIVE);
                break;
        }
        
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        mConfig = FastChargeConfig.getInstance(this);

        updateUI();

        IntentFilter filter = new IntentFilter(mConfig.ACTION_FAST_CHARGE_SERVICE_CHANGED);
        registerReceiver(mServiceStateReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        unregisterReceiver(mServiceStateReceiver);
    }

    @Override
    public void onClick() {
        super.onClick();
        mInternalModeChange = true;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String currentMode = mConfig.getCurrentMode();
        String nextMode;
        
        switch (currentMode) {
            case FastChargeConfig.MODE_SLOW:
                nextMode = FastChargeConfig.MODE_FAST;
                break;
            case FastChargeConfig.MODE_FAST:
                nextMode = FastChargeConfig.MODE_SUPER_FAST;
                break;
            case FastChargeConfig.MODE_SUPER_FAST:
                nextMode = FastChargeConfig.MODE_SLOW;
                break;
            default:
                nextMode = FastChargeConfig.MODE_FAST;
                break;
        }

        FileUtils.writeLine(mConfig.getFastChargePath(), nextMode);

        sharedPrefs.edit().putString(mConfig.FASTCHARGE_KEY, nextMode).commit();

        Intent intent = new Intent(mConfig.ACTION_FAST_CHARGE_SERVICE_CHANGED);
        intent.putExtra(mConfig.EXTRA_FAST_CHARGE_MODE, nextMode);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        this.sendBroadcastAsUser(intent, UserHandle.CURRENT);

        updateUI();
    }

    private void tryStopService() {
        if (mFastChargeIntent == null) return;
        this.stopService(mFastChargeIntent);
        mFastChargeIntent = null;
    }
}

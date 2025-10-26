/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.fastcharge;

import android.content.Context;

import com.android.fastcharge.utils.FileUtils;

public class FastChargeConfig {

    private static FastChargeConfig instance = null;

    public static FastChargeConfig getInstance(Context context) {

        if (instance == null) {
            instance = new FastChargeConfig(context.getApplicationContext());
        }

        return instance;
    }

    public static final String FASTCHARGE_KEY = "wired_charging_mode";

    public static final String FASTCHARGE_PATH = "/sys/class/qcom-battery/fastcharge_enable";

    public static final String MODE_SLOW = "0";
    public static final String MODE_FAST = "1";
    public static final String MODE_SUPER_FAST = "2";

    public static final String ACTION_FAST_CHARGE_SERVICE_CHANGED = "com.android.fastcharge.FAST_CHARGE_SERVICE_CHANGED";
    public static final String EXTRA_FAST_CHARGE_MODE = "fastchargingmode";

    private FastChargeConfig(Context context) {
    }

    public String getFastChargePath() {
        return FASTCHARGE_PATH;
    }

    public String getCurrentMode() {
        String value = FileUtils.readOneLine(FASTCHARGE_PATH);
        if (value != null) {
            value = value.trim();
            if (value.equals(MODE_SLOW) || value.equals(MODE_FAST) || value.equals(MODE_SUPER_FAST)) {
                return value;
            }
        }
        return MODE_FAST;
    }
 }

/*
 * SPDX-FileCopyrightText: 2025 kenway214
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.fastcharge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.fastcharge.R;

public class ImagePreference extends Preference {

    private ImageView imageView;

    public ImagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.fastcharge_image_layout);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        imageView = (ImageView) holder.findViewById(R.id.fastcharge_image);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.ic_fastcharge_illustration);
        }
    }
}

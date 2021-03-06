/*
 * SPDX-FileCopyrightText: 2020, microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.nearby.exposurenotification

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class ExposurePreferences(private val context: Context) {
    private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var scannerEnabled
        get() = preferences.getBoolean(PREF_SCANNER_ENABLED, false)
        set(newStatus) {
            preferences.edit().putBoolean(PREF_SCANNER_ENABLED, newStatus).commit()
            if (newStatus) {
                context.sendOrderedBroadcast(Intent(context, ServiceTrigger::class.java), null)
            } else {
                context.stopService(Intent(context, ScannerService::class.java))
                context.stopService(Intent(context, AdvertiserService::class.java))
            }
        }

    val advertiserEnabled
        get() = scannerEnabled

    companion object {
        private const val PREF_SCANNER_ENABLED = "exposure_scanner_enabled"
    }
}

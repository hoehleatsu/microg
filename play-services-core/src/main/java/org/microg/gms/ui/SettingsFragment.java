package org.microg.gms.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.R;

import org.microg.gms.checkin.CheckinPrefs;
import org.microg.gms.gcm.GcmDatabase;
import org.microg.gms.gcm.GcmPrefs;
import org.microg.gms.nearby.exposurenotification.ExposurePreferences;
import org.microg.gms.snet.SafetyNetPrefs;
import org.microg.tools.ui.ResourceSettingsFragment;

public class SettingsFragment extends ResourceSettingsFragment {

    public static final String PREF_ABOUT = "pref_about";
    public static final String PREF_GCM = "pref_gcm";
    public static final String PREF_SNET = "pref_snet";
    public static final String PREF_UNIFIEDNLP = "pref_unifiednlp";
    public static final String PREF_CHECKIN = "pref_checkin";
    public static final String PREF_EXPOSURE = "pref_exposure";

    public SettingsFragment() {
        preferencesResource = R.xml.preferences_start;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        updateDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDetails();
    }

    private void updateDetails() {
        findPreference(PREF_ABOUT).setSummary(getString(R.string.about_version_str, AboutFragment.getSelfVersion(getContext())));
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(preference -> {
            UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openAbout, null);
            return true;
        });
        if (GcmPrefs.get(getContext()).isEnabled()) {
            GcmDatabase database = new GcmDatabase(getContext());
            int regCount = database.getRegistrationList().size();
            database.close();
            findPreference(PREF_GCM).setSummary(getString(R.string.service_status_enabled_short) + " - " + getResources().getQuantityString(R.plurals.gcm_registered_apps_counter, regCount, regCount));
        } else {
            findPreference(PREF_GCM).setSummary(R.string.service_status_disabled_short);
        }
        findPreference(PREF_GCM).setOnPreferenceClickListener(preference -> {
            UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openGcmSettings, null);
            return true;
        });

        if (SafetyNetPrefs.get(getContext()).isEnabled()) {
            String snet_info = "";

            if (SafetyNetPrefs.get(getContext()).isOfficial()) {
                snet_info = getString(R.string.pref_snet_status_official_info);
            } else if (SafetyNetPrefs.get(getContext()).isSelfSigned()) {
                snet_info = getString(R.string.pref_snet_status_self_signed_info);
            } else if (SafetyNetPrefs.get(getContext()).isThirdParty()) {
                snet_info = getString(R.string.pref_snet_status_third_party_info);
            }

            findPreference(PREF_SNET).setSummary(getString(R.string.service_status_enabled_short));
        } else {
            findPreference(PREF_SNET).setSummary(R.string.service_status_disabled_short);
        }
        findPreference(PREF_SNET).setOnPreferenceClickListener(preference -> {
            UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openSafetyNetSettings, null);
            return true;
        });

//            Preferences unifiedNlPrefs = new Preferences(getContext());
//            int backendCount = TextUtils.isEmpty(unifiedNlPrefs.getLocationBackends()) ? 0 :
//                    Preferences.splitBackendString(unifiedNlPrefs.getLocationBackends()).length;
//            backendCount += TextUtils.isEmpty(unifiedNlPrefs.getGeocoderBackends()) ? 0 :
//                    Preferences.splitBackendString(unifiedNlPrefs.getGeocoderBackends()).length;
//            findPreference(PREF_UNIFIEDNLP).setSummary(getResources().getQuantityString(R.plurals.pref_unifiednlp_summary, backendCount, backendCount));
        findPreference(PREF_UNIFIEDNLP).setOnPreferenceClickListener(preference -> {
            UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openUnifiedNlpSettings, null);
            return true;
        });
        if (Build.VERSION.SDK_INT >= 21) {
            findPreference(PREF_EXPOSURE).setVisible(true);
            if (new ExposurePreferences(getContext()).getScannerEnabled()) {
                findPreference(PREF_EXPOSURE).setSummary(getString(R.string.service_status_enabled_short));
            } else {
                findPreference(PREF_EXPOSURE).setSummary(R.string.service_status_disabled_short);
            }
            findPreference(PREF_EXPOSURE).setOnPreferenceClickListener(preference -> {
                UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openExposureNotificationSettings, null);
                return true;
            });
        } else {
            findPreference(PREF_EXPOSURE).setVisible(false);
        }

        boolean checkinEnabled = CheckinPrefs.get(getContext()).isEnabled();
        findPreference(PREF_CHECKIN).setSummary(checkinEnabled ? R.string.service_status_enabled_short : R.string.service_status_disabled_short);
        findPreference(PREF_CHECKIN).setOnPreferenceClickListener(preference -> {
            UtilsKt.navigate(NavHostFragment.findNavController(SettingsFragment.this), getContext(), R.id.openCheckinSettings, null);
            return true;
        });
    }
}

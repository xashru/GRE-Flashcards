package com.ashru.greflashcards;

import com.ashru.greflashcards.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static SharedPreferences prefs;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference about = (Preference) findPreference(Constants.ABOUT_KEY);
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(getApplicationContext(),
						DialogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_DIALOG_KEY,
						Constants.DIALOG_ABOUT);
				intent.putExtras(bundle);
				startActivity(intent);
				return true;
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		SharedPreferences.Editor editor = prefs.edit();
		if (prefs.getBoolean(Constants.RESET_KEY, false)) {
			// apply reset, and then set the pref-value back to false
			MainActivity.db.resetWords();
			editor.putBoolean(Constants.RESET_KEY, false);
			editor.commit();
		}
	}

}

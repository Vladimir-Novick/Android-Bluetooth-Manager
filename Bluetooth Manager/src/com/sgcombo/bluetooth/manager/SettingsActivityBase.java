package com.sgcombo.bluetooth.manager;

import java.io.File;
import java.lang.reflect.Method;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
/*
Copyright (C) 2012 by Vladimir Novick http://www.linkedin.com/in/vladimirnovick , 

     vlad.novick@gmail.com , http://www.sgcombo.com , https://github.com/Vladimir-Novick

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/
public class SettingsActivityBase extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {


	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initSummary();
		

		
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	private void initSummary() {
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
			Preference preference = getPreferenceScreen().getPreference(i);
			if (preference instanceof PreferenceGroup) {
				PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
				for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
					updatePrefSummary(preferenceGroup.getPreference(j));
				}
			} else {
				updatePrefSummary(preference);
			}
		}
	}



	


	
    protected int getXMLConfiguration(){
    	return R.xml.settings;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(getXMLConfiguration());
		

		initSummary();
	}





	protected void setListPreference(String listName, String[] ListEntries,
			String defaultName,SharedPreferences preferences) {
		String keyPr = listName ;
		String value = preferences.getString(keyPr, "");


		@SuppressWarnings("deprecation")
		ListPreference listPreferenceImageMode = (ListPreference) findPreference(listName);
		if (ListEntries == null){
			listPreferenceImageMode.setEnabled(false);
			return;
		}
		listPreferenceImageMode.setKey(keyPr);

		if ((ListEntries == null) || (ListEntries.length == 0 )) {
			listPreferenceImageMode.setEnabled(false);
		} else {
			listPreferenceImageMode.setValue(value);
			CharSequence entriesMode[] = new String[ListEntries.length];
			CharSequence entryModeValues[] = new String[ListEntries.length];
			int ip = 0;
			for (String mode : ListEntries) {
				entriesMode[ip] = mode;
				entryModeValues[ip] = mode;
				ip++;
			}
			listPreferenceImageMode.setEntries(entriesMode);
			listPreferenceImageMode.setEntryValues(entryModeValues);
		}
	}

	private void updatePrefSummary(Preference p) {
		if (p instanceof ListPreference) {
			try {
				ListPreference listPref = (ListPreference) p;
				if (listPref.isEnabled() ) {
				String s = listPref.getValue();
				CharSequence entry = listPref.getEntry();
				CharSequence summary = listPref.getSummary();
				String sum = summary.toString();
				String[] old = sum.split(" <- ");
				String cont = old[0];
				if (old.length > 1) cont = old[1];
				//entry = entry + " <- " + cont;
				listPref.setSummary(entry);
				}
			} catch (Exception e) {
			}
		}
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			if (editTextPref.isEnabled()) {
			if (p.getTitle().toString().contains("assword")) {
				String s = editTextPref.getText();
				if ((s != "") && (s != null)) {
					p.setSummary("******");
				}
			} else {
				String s = editTextPref.getText();
				if ((s != "") && (s != null)) {
					p.setSummary(editTextPref.getText());
				}
			}
			}
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Object obj = findPreference(key);
		if (obj instanceof ListPreference) {
			String val = sharedPreferences.getString(key, "");
			ListPreference l = (ListPreference)obj;
			CharSequence entry = l.getEntry();
			l.setSummary(entry);
		}
		
		if (obj instanceof EditTextPreference) {
			String val = sharedPreferences.getString(key, "");
			EditTextPreference l = (EditTextPreference)obj;
			CharSequence entry = l.getText();
			if (l.getTitle().toString().contains("assword")) {
				 l.setSummary("******");
			} else {
				  l.setSummary(entry);
			}
		}
		

	}

}

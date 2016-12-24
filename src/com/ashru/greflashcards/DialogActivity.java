package com.ashru.greflashcards;

import com.ashru.greflashcards.R;

import android.app.Activity;
import android.os.Bundle;

public class DialogActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		int dialog = extras.getInt(Constants.BUNDLE_DIALOG_KEY);
		if (dialog == Constants.DIALOG_HELP) {
			setContentView(R.layout.help_window);
			setTitle("Help");
		} else if (dialog == Constants.DIALOG_ABOUT) {
			setContentView(R.layout.about_window);
			setTitle("About");
		}
	}

}

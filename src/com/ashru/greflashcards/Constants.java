package com.ashru.greflashcards;

import android.content.Context;

import com.ashru.greflashcards.R;

public class Constants {

	/** bundle key to pass a word */
	public static final String BUNDLE_PASS_WORD_KEY = "WORD_NUMBER";
	/** bundle key to pass a word number in list */
	public static final String BUNDLE_PASS_WORD_NUMBER_KEY = "WORD_NUMBER";
	/** bundle key to pass a list of words */
	public static final String BUNDLE_PASS_WORD_LIST_KEY = "SHOW_WORD_LIST";
	/** bundle key to pass whether to show rating bar or not */
	public static final String BUNDLE_PASS_RATING_KEY = "SHOW_RATING";
	/** bundle key for dialog */
	public static final String BUNDLE_DIALOG_KEY = "DIALOG_TYPE";

	/** total words in database */
	public static final int TOTAL_WORDS = 1000;
	public static final int DIALOG_HELP = 1;
	public static final int DIALOG_ABOUT = 2;
	public static final float PROBABILITY_FOR_LEARNED_WORDS = 0.05f;

	public static String RATING_KEY;
	public static String RESET_KEY;
	public static String ABOUT_KEY;

	public static void newPrefKeys(Context context) {
		RATING_KEY = context.getResources().getString(R.string.pref_rating);
		RESET_KEY = context.getResources().getString(R.string.pref_reset);
		ABOUT_KEY = context.getResources().getString(R.string.pref_about);
	}
}

package com.ashru.greflashcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import com.ashru.greflashcards.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private EditText wordText, wordMeaning, wordExample, wordNote;
	private TextView wordTime, wordProgressText;
	private Button buttonShow, buttonKnow, buttonDunno, buttonEdit, buttonSave,
			buttonCancel;
	private RatingBar wordRating;
	private ProgressBar wordProgressBar;

	private ViewStatus viewStatus;

	/** static word database */
	public static MySQLiteHelper db;

	private Word currentWord;
	private int totalWords, learnedWords;
	private float averageRating;

	private static SharedPreferences prefs;

	private Random generator = new Random();
	private ArrayList<Word> wordList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Constants.newPrefKeys(getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		db = new MySQLiteHelper(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (db.isEmpty()) {
			new LoadViewTask().execute();
			currentWord = Word.getInitWord();
		} else {
			getListStatus();
			currentWord = randomizeWord();
		}

		viewStatus = ViewStatus.SHOW_WORD;

		wordText = (EditText) findViewById(R.id.word);
		wordMeaning = (EditText) findViewById(R.id.word_meaning);
		wordExample = (EditText) findViewById(R.id.word_example);
		wordNote = (EditText) findViewById(R.id.word_note);
		wordTime = (TextView) findViewById(R.id.word_time);
		wordProgressText = (TextView) findViewById(R.id.word_progress_text);

		buttonShow = (Button) findViewById(R.id.button_show_meaning);
		buttonKnow = (Button) findViewById(R.id.button_know);
		buttonDunno = (Button) findViewById(R.id.button_dunno);
		buttonEdit = (Button) findViewById(R.id.button_edit);
		buttonSave = (Button) findViewById(R.id.button_save);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		wordRating = (RatingBar) findViewById(R.id.word_rating);
		wordProgressBar = (ProgressBar) findViewById(R.id.word_progress);

	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (!db.isEmpty()) {
				getListStatus();
				// reloading word if it was edited in another actvity
				currentWord = db.getWord(currentWord.getId());
			}
		} catch (Exception e) {
			// 
			return;
		}
		setView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			startActivity(new Intent(this, AddWordActivity.class));
			return true;
		case R.id.action_viewall:
			Intent intentViewAll = new Intent(getApplicationContext(),
					ViewAllWordsActivity.class);
			startActivity(intentViewAll);
			return true;
		case R.id.action_search:
			Intent searchIntent = new Intent(getApplicationContext(),
					SearchActivity.class);
			startActivity(searchIntent);
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.action_help:
			Intent intentHelp = new Intent(getApplicationContext(),
					DialogActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.BUNDLE_DIALOG_KEY, Constants.DIALOG_HELP);
			intentHelp.putExtras(bundle);
			startActivity(intentHelp);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_show_meaning:
			viewStatus = ViewStatus.SHOW_MEANING;
			setView();
			break;
		case R.id.button_know:
			updateWord(currentWord, true);
			db.updateWord(currentWord);
			getListStatus();
			currentWord = randomizeWord();
			viewStatus = ViewStatus.SHOW_WORD;
			setView();
			break;
		case R.id.button_dunno:
			updateWord(currentWord, false);
			db.updateWord(currentWord);
			getListStatus();
			currentWord = randomizeWord();
			viewStatus = ViewStatus.SHOW_WORD;
			setView();
			break;
		case R.id.button_edit:
			Intent intent = new Intent(MainActivity.this,
					EditWordActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.BUNDLE_PASS_WORD_KEY, currentWord);
			bundle.putBoolean(Constants.BUNDLE_PASS_RATING_KEY, true);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		case R.id.button_save:
			updateWord(currentWord);
			db.updateWord(currentWord);
			getListStatus();
			currentWord = randomizeWord();
			viewStatus = ViewStatus.SHOW_WORD;
			setView();
			break;
		case R.id.button_cancel:
			viewStatus = ViewStatus.SHOW_MEANING;
			setView();
			break;
		}

	}

	/**
	 * get updated word from edit word activity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			currentWord = extras.getParcelable(Constants.BUNDLE_PASS_WORD_KEY);
			setView();
		}
	}

	/**
	 * update word from current parameters
	 * 
	 * @param word
	 *            word to update
	 */
	private void updateWord(Word word) {
		word.setWord(wordText.getText().toString());
		word.setWordMeaning(wordMeaning.getText().toString());
		word.setWordExample(wordExample.getText().toString());
		word.setWordNote(wordNote.getText().toString());
		float rating = wordRating.getRating();
		rating = rating * Word.MAX_RATING / (float) Word.TOTAL_RATING_STARS;
		word.setRating(rating);
		word.setLastStudyTime((long) (System.currentTimeMillis() / 1000.0 / 60.0));
	}

	/**
	 * get day elapsed since last study
	 * 
	 * @param time
	 *            last time in minutes
	 * @return elapsed day(s)
	 */
	private int getDaysElapsed(Word word) {
		long lastTime = word.getLastStudyTime();
		long curTime = (long) (System.currentTimeMillis() / 1000.0 / 60.0);
		return (int) ((int) (curTime - lastTime) / 60.0 / 24.0);
	}

	/**
	 * get hours elapsed since last study
	 * 
	 * @param time
	 *            last time in minutes
	 * @return elapsed day(s)
	 */
	private int getHoursElapsed(Word word) {
		long lastTime = word.getLastStudyTime();
		long curTime = (long) (System.currentTimeMillis() / 1000.0 / 60.0);
		return (int) ((int) (curTime - lastTime) / 60.0);
	}

	/**
	 * set text in wordTime textView
	 * 
	 * @param word
	 *            time of which word
	 */
	private void setWordTimeText(Word word) {
		if (word.getLastStudyTime() == 0) {
			wordTime.setText("Time since study: " + 0 + " day");
			return;
		}
		int days = getDaysElapsed(word);
		if (days <= 1) {
			wordTime.setText("Time since study: " + days + " day");
		} else {
			wordTime.setText("Time since study: " + days + " days");
		}
	}

	/**
	 * randomly select a word from word table
	 * 
	 * @return selected word
	 */
	private Word randomizeWord() {
		float aveageRating = averageRating;
		int size = wordList.size();
		while (true) {
			int wordNumber = generator.nextInt(size);
			Word word = wordList.get(wordNumber);
			int wordProbability = (int) (Word.MAX_RATING + (word.getRating() - aveageRating));
			if (word.getLearned() == 1) {
				wordProbability *= Constants.PROBABILITY_FOR_LEARNED_WORDS;
			}
			int random = generator.nextInt((int) (Word.MAX_RATING * 2));
			if (random < wordProbability) {
				return word;
			}
		}
	}

	/**
	 * get the total words,learned words,rating of words from list
	 */
	private void getListStatus() {
		wordList = db.getAllWords();
		long sum = 0;
		int learned = 0;
		int size = wordList.size();
		for (int i = 0; i < size; i++) {
			Word word = wordList.get(i);
			sum += word.getRating();
			if (word.getLearned() == 1) {
				learned++;
			}
		}
		this.totalWords = size;
		this.learnedWords = learned;
		this.averageRating = sum / size;
	}

	/**
	 * update time of the word time and rating
	 * 
	 * @param word
	 *            word to update
	 * 
	 * @param know
	 *            whether the word is known at this point
	 */
	private void updateWord(Word word, boolean know) {
		int hours = getHoursElapsed(word);
		float curRating = word.getRating();
		float ratingChange;
		if (know == true) {
			word.setLearned(1);
			if (hours < 50) {
				ratingChange = -hours * 0.10f;
			} else if (hours < 100) {
				ratingChange = -(15 + (hours - 50) * 0.25f);
			} else if (hours < 200) {
				ratingChange = -(50 + (hours - 100) * 0.25f);
			} else if (hours < 500) {
				ratingChange = -(75 + (hours - 200) * 0.10f);
			} else {
				ratingChange = -(100 + (hours - 500) * 0.05f);
			}
			if (ratingChange <= -400) {
				ratingChange = -400;
			}
		} else {
			word.setLearned(0);
			if (hours <= 1) {
				ratingChange = 25 * Word.MAX_RATING / curRating;
			} else if (hours <= 6) {
				ratingChange = 19 * Word.MAX_RATING / curRating;
			} else if (hours <= 24) {
				ratingChange = 14 * Word.MAX_RATING / curRating;
			} else if (hours <= 50) {
				ratingChange = 10 * Word.MAX_RATING / curRating;
			} else if (hours <= 100) {
				ratingChange = 7 * Word.MAX_RATING / curRating;
			} else {
				ratingChange = 5 * Word.MAX_RATING / curRating;
			}
		}
		int newRating = (int) (curRating + ratingChange);
		if (newRating >= 500) {
			newRating = 500;
		} else if (newRating <= 25) {
			newRating = 25;
		}
		word.setRating(newRating);
		word.setLastStudyTime((long) (System.currentTimeMillis() / 1000.0 / 60.0));
	}

	/**
	 * set stars in word rating bar
	 * 
	 * @param word
	 *            related word
	 */
	private void setRatingBarView(Word word) {
		float rating = Word.TOTAL_RATING_STARS * word.getRating()
				/ Word.MAX_RATING;
		wordRating.setRating(rating);
	}

	/**
	 * set progress bar
	 * 
	 * @param wordText
	 *            related word
	 */
	private void setProgressBarView() {
		wordProgressText.setText("Words learned: " + learnedWords + "/"
				+ totalWords);
		wordProgressBar
				.setProgress((int) (learnedWords * 100.0 / (float) totalWords));
	}

	private void setView() {

		wordText.setText(currentWord.getWord());
		wordMeaning.setText(Util.setTextStyleItalic(currentWord
				.getWordMeaning()));
		wordExample.setText(Util.setTextStyleItalic(currentWord
				.getWordExample()));
		wordNote.setText(Util.setTextStyleItalic(currentWord.getWordNote()));
		setWordTimeText(currentWord);
		setRatingBarView(currentWord);
		setProgressBarView();

		switch (viewStatus) {
		case SHOW_WORD:
			wordText.setEnabled(false);
			showRating();
			wordMeaning.setVisibility(View.GONE);
			wordExample.setVisibility(View.GONE);
			wordNote.setVisibility(View.GONE);
			wordTime.setVisibility(View.GONE);
			wordProgressText.setVisibility(View.VISIBLE);
			wordProgressBar.setVisibility(View.VISIBLE);
			buttonShow.setVisibility(View.VISIBLE);
			buttonKnow.setVisibility(View.GONE);
			buttonDunno.setVisibility(View.GONE);
			buttonEdit.setVisibility(View.GONE);
			buttonSave.setVisibility(View.GONE);
			buttonCancel.setVisibility(View.GONE);
			break;
		case SHOW_MEANING:
			wordText.setEnabled(false);
			showRating();
			wordMeaning.setVisibility(View.VISIBLE);
			wordMeaning.setEnabled(false);
			wordExample.setVisibility(View.VISIBLE);
			wordExample.setEnabled(false);
			wordNote.setVisibility(View.VISIBLE);
			wordNote.setEnabled(false);
			wordTime.setVisibility(View.VISIBLE);
			wordProgressText.setVisibility(View.GONE);
			wordProgressBar.setVisibility(View.GONE);
			buttonShow.setVisibility(View.GONE);
			buttonKnow.setVisibility(View.VISIBLE);
			buttonDunno.setVisibility(View.VISIBLE);
			buttonEdit.setVisibility(View.VISIBLE);
			buttonSave.setVisibility(View.GONE);
			buttonCancel.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * shows or hides rating bar
	 */
	private void showRating() {
		boolean show = prefs.getBoolean(Constants.RATING_KEY, true);
		if (show) {
			wordRating.setVisibility(View.VISIBLE);
		} else {
			wordRating.setVisibility(View.GONE);
		}
		wordRating.setEnabled(false);
	}

	/**
	 * support class to initialize database
	 */
	private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
		private InputStream is;
		private String line;
		private BufferedReader br;
		private ProgressDialog progressDialog;

		// Before running code in separate thread
		@Override
		protected void onPreExecute() {

			// load word data textfile
			try {
				is = getAssets().open("word_list.txt");
				br = new BufferedReader(new InputStreamReader(is));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// Create a new progress dialog
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Initializing database, please wait...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.setMax(Constants.TOTAL_WORDS);
			progressDialog.setProgress(0);
			progressDialog.show();
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			synchronized (this) {
				int loadCount = 0;
				try {
					while ((line = br.readLine()) != null) {
						String[] word = line.split("\\|");
						db.addWord(new Word(word[0], word[1], word[2]));
						loadCount++;
						publishProgress(loadCount);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		// Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setProgress(values[0]);
		}

		// after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			wordList = db.getAllWords();
			getListStatus();
			currentWord = randomizeWord();
			progressDialog.dismiss();
			setView();
		}
	}

}

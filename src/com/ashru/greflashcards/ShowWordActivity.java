package com.ashru.greflashcards;

import java.util.ArrayList;

import com.ashru.greflashcards.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ShowWordActivity extends Activity implements OnClickListener {

	private Word currentWord;
	private int currentWordPosition;
	private TextView word, wordMeaning, wordExample, wordNote;
	private ArrayList<Word> wordList;
	private int wordListSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_word);

		Bundle extras = getIntent().getExtras();
		currentWordPosition = extras
				.getInt(Constants.BUNDLE_PASS_WORD_NUMBER_KEY);
		wordList = extras
				.getParcelableArrayList(Constants.BUNDLE_PASS_WORD_LIST_KEY);
		currentWord = wordList.get(currentWordPosition);
		wordListSize = wordList.size();

		word = (TextView) findViewById(R.id.word);
		wordMeaning = (TextView) findViewById(R.id.word_meaning);
		wordExample = (TextView) findViewById(R.id.word_example);
		wordNote = (TextView) findViewById(R.id.word_note);

		setView();
	}

	private void setView() {
		word.setText(currentWord.getWord());
		wordMeaning.setText(Util.setTextStyleItalic(currentWord
				.getWordMeaning()));
		wordExample.setText(Util.setTextStyleItalic(currentWord
				.getWordExample()));
		wordNote.setText(Util.setTextStyleItalic(currentWord.getWordNote()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			this.finish();
			break;
		case R.id.button_next:
			if (currentWordPosition != wordListSize - 1) {
				currentWord = wordList.get(currentWordPosition + 1);
				currentWordPosition++;
			} else {
				currentWord = wordList.get(0);
				currentWordPosition = 0;
			}
			setView();
			break;
		case R.id.button_edit:
			Intent intent = new Intent(ShowWordActivity.this,
					EditWordActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.BUNDLE_PASS_WORD_KEY, currentWord);
			bundle.putBoolean(Constants.BUNDLE_PASS_RATING_KEY, false);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			currentWord = extras.getParcelable(Constants.BUNDLE_PASS_WORD_KEY);
			setView();
		}
	}

}

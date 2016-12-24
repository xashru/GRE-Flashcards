package com.ashru.greflashcards;

import com.ashru.greflashcards.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class EditWordActivity extends Activity implements OnClickListener {

	private Word currentWord;
	private EditText wordText, wordMeaning, wordExample, wordNote;
	private RatingBar wordRating;
	private boolean showRating;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_word);

		Bundle extras = getIntent().getExtras();
		currentWord = extras.getParcelable(Constants.BUNDLE_PASS_WORD_KEY);
		showRating = extras.getBoolean(Constants.BUNDLE_PASS_RATING_KEY);

		wordText = (EditText) findViewById(R.id.word);
		wordMeaning = (EditText) findViewById(R.id.word_meaning);
		wordExample = (EditText) findViewById(R.id.word_example);
		wordNote = (EditText) findViewById(R.id.word_note);
		wordRating = (RatingBar) findViewById(R.id.word_rating);

		setView();

	}

	private void setView() {
		wordText.setText(currentWord.getWord());
		wordMeaning.setText(Util.setTextStyleItalic(currentWord
				.getWordMeaning()));
		wordExample.setText(Util.setTextStyleItalic(currentWord
				.getWordExample()));
		wordNote.setText(Util.setTextStyleItalic(currentWord.getWordNote()));
		if (showRating == false) {
			wordRating.setVisibility(View.GONE);
		} else {
			wordRating.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_save:
			if (wordText.getText().toString().equals("")) {
				Toast.makeText(this, "Word cannot be empty!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			updateWord(currentWord);
			MainActivity.db.updateWord(currentWord);

			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.BUNDLE_PASS_WORD_KEY, currentWord);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(Activity.RESULT_OK, intent);
			this.finish();
			break;
		case R.id.button_cancel:
			this.finish();
			break;
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
		if (showRating) {
			float rating = wordRating.getRating();
			rating = rating * Word.MAX_RATING / (float) Word.TOTAL_RATING_STARS;
			word.setRating(rating);
		}
	}

}

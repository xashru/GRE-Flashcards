package com.ashru.greflashcards;

import com.ashru.greflashcards.R; 

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class AddWordActivity extends Activity implements OnClickListener {

	private EditText wordText, wordMeaning, wordExample, wordSpecial;
	private RatingBar wordRating;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_word);

		wordText = (EditText) findViewById(R.id.word);
		wordMeaning = (EditText) findViewById(R.id.word_meaning);
		wordExample = (EditText) findViewById(R.id.word_example);
		wordSpecial = (EditText) findViewById(R.id.word_note);
		wordRating = (RatingBar) findViewById(R.id.word_rating);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			if (wordText.getText().toString().equals("")) {
				Toast.makeText(this, "Word cannot be empty!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			createWord();
			this.finish();
			break;
		}
	}

	/**
	 * ceate a new word and add it to database
	 */
	private void createWord() {
		String wordString = wordText.getText().toString();
		String meaning = wordMeaning.getText().toString();
		String example = wordExample.getText().toString();
		String special = wordSpecial.getText().toString();
		float rating = wordRating.getRating();
		rating = rating * Word.MAX_RATING / (float) Word.TOTAL_RATING_STARS;
		Word word = new Word(wordString, meaning, example, special, rating);
		MainActivity.db.addWord(word);
	}

}

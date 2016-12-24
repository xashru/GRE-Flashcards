package com.ashru.greflashcards;

import java.util.ArrayList;
import java.util.Collections;
import com.ashru.greflashcards.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ViewAllWordsActivity extends Activity {

	private ArrayList<Word> wordList;
	private ListView listView;
	// vertical position of scroll bar
	private static int scrollIndex, scrollTop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_list_layout);
		listView = (ListView) findViewById(R.id.listView);

	}

	@Override
	public void onResume() {
		super.onResume();
		wordList = MainActivity.db.getAllWords();
		Collections.sort(wordList);
		initializeListView();
	}

	private void initializeListView() {
		CustomAdapter adapter = new CustomAdapter(this, wordList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setSelectionFromTop(scrollIndex, scrollTop);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parentAdapter, View view,
				int position, long id) {

			scrollIndex = listView.getFirstVisiblePosition();
			View v = listView.getChildAt(0);
			scrollTop = (v == null) ? 0 : v.getTop();

			Intent intent = new Intent(getApplicationContext(),
					ShowWordActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.BUNDLE_PASS_WORD_NUMBER_KEY, position);
			bundle.putParcelableArrayList(Constants.BUNDLE_PASS_WORD_LIST_KEY,
					wordList);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	@Override
	public void onBackPressed() {
		scrollIndex = listView.getFirstVisiblePosition();
		View v = listView.getChildAt(0);
		scrollTop = (v == null) ? 0 : v.getTop();
		super.onBackPressed();
	}
}
package com.ashru.greflashcards;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ashru.greflashcards.R;

public class SearchActivity extends ActionBarActivity implements
		OnQueryTextListener {

	private SearchView searchView;
	private ArrayList<Word> queryList;
	private String searchQuery = "";
	private ListView listView;
	private int scrollIndex, scrollTop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_list_layout);
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public void onResume() {
		super.onResume();
		queryList = MainActivity.db.getMatchingWords(searchQuery);
		Collections.sort(queryList);
		addWordsToView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_options_menu, menu);

		// Associate searchable configuration with the SearchView
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setFocusable(true);
		searchView.setIconified(false);
		searchView.requestFocusFromTouch();
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * add all words from table in layout
	 */
	private void addWordsToView() {
		CustomAdapter adapter = new CustomAdapter(this, queryList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setSelectionFromTop(scrollIndex, scrollTop);
	}

	@Override
	public boolean onQueryTextChange(String query) {
		queryList = MainActivity.db.getMatchingWords(query);
		Collections.sort(queryList);
		addWordsToView();
		searchQuery = query;
		return false;
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
					queryList);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		searchView.clearFocus();
		return false;
	}

}

package com.ashru.greflashcards;

import java.util.ArrayList;

import com.ashru.greflashcards.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Word> {

	Word[] wordList = null;
	Context context;
	LayoutInflater inflater;

	public CustomAdapter(Context context, ArrayList<Word> wordList2) {
		super(context, R.layout.list_item_layout, wordList2);
		this.context = context;
		int size = wordList2.size();
		wordList = new Word[size];
		for (int i = 0; i < size; i++) {
			wordList[i] = wordList2.get(i);
		}
		inflater = ((Activity) context).getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout) convertView;
		if (view == null) {
			view = (LinearLayout) inflater.inflate(R.layout.list_item_layout,
					parent, false);
		}
		TextView tv = (TextView) view.findViewById(R.id.list_item_text);
		tv.setText(getItem(position).getWord());
		CheckBox cb = (CheckBox) view.findViewById(R.id.list_item_checkbox);
		if (wordList[position].getLearned() == 1) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}
		cb.setClickable(false);
		return view;
	}

}

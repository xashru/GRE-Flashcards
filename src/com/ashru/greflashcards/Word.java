package com.ashru.greflashcards;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Comparable<Word>, Parcelable {

	private String word, wordMeaning, wordExample, wordNote;
	private int id, learned;
	private float rating;
	/** time when the word was last studied in minutes */
	private long lastStudyTime;
	public static final float MAX_RATING = 500.0f;
	public static final int TOTAL_RATING_STARS = 5;
	/** word to show when loading initial database */
	private static final Word INIT_WORD = new Word("GRE",
			"Graduate Record Examination");

	public Word() {

	}

	public Word(String word, String meaning) {
		this.word = word;
		this.wordMeaning = meaning;
		this.wordExample = "";
		this.wordNote = "";
		this.rating = MAX_RATING;
		this.learned = 0;
		this.lastStudyTime = 0;
	}

	public Word(String word, String meaning, String example) {
		this.word = word;
		this.wordMeaning = meaning;
		this.wordExample = example;
		this.wordNote = "";
		this.rating = MAX_RATING;
		this.learned = 0;
		this.lastStudyTime = (long) (System.currentTimeMillis() / 1000.0 / 60.0);
	}

	public Word(String word, String meaning, String example, float rating) {
		this.word = word;
		this.wordMeaning = meaning;
		this.wordExample = example;
		this.wordNote = "";
		this.rating = rating;
		this.learned = 0;
		this.lastStudyTime = (long) (System.currentTimeMillis() / 1000.0 / 60.0);
	}

	public Word(String word, String meaning, String example, String note,
			float rating) {
		this.word = word;
		this.wordMeaning = meaning;
		this.wordExample = example;
		this.wordNote = note;
		this.rating = rating;
		this.learned = 0;
		this.lastStudyTime = (long) (System.currentTimeMillis() / 1000.0 / 60.0);
	}

	public Word(Parcel in) {
		this.id = in.readInt();
		this.word = in.readString();
		this.wordMeaning = in.readString();
		this.wordExample = in.readString();
		this.wordNote = in.readString();
		this.lastStudyTime = in.readLong();
		this.rating = in.readFloat();
		this.learned = in.readInt();
	}

	@Override
	public String toString() {
		return "Word [id=" + id + ", word=" + this.word + ", meaning="
				+ this.wordMeaning + "]";
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordMeaning() {
		return wordMeaning;
	}

	public void setWordMeaning(String wordMeaning) {
		this.wordMeaning = wordMeaning;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWordExample() {
		return wordExample;
	}

	public void setWordExample(String wordExample) {
		this.wordExample = wordExample;
	}

	public String getWordNote() {
		return wordNote;
	}

	public void setWordNote(String wordNote) {
		this.wordNote = wordNote;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public int getLearned() {
		return learned;
	}

	public void setLearned(int learned) {
		this.learned = learned;
	}

	public long getLastStudyTime() {
		return lastStudyTime;
	}

	public void setLastStudyTime(long lastStudyTime) {
		this.lastStudyTime = lastStudyTime;
	}

	public static final Word getInitWord() {
		return INIT_WORD;
	}

	@Override
	public int compareTo(Word another) {
		Locale loc = Locale.getDefault();
		String thisWord = this.word.toUpperCase(loc);
		String anotherWord = another.word.toUpperCase(loc);
		if (thisWord.compareTo(anotherWord) < 0) {
			return -1;
		} else if (thisWord.compareTo(anotherWord) > 0) {
			return 1;
		} else
			return 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.word);
		dest.writeString(this.wordMeaning);
		dest.writeString(this.wordExample);
		dest.writeString(this.wordNote);
		dest.writeLong(this.lastStudyTime);
		dest.writeFloat(this.rating);
		dest.writeInt(this.learned);
	}

	public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
		public Word createFromParcel(Parcel in) {
			return new Word(in);
		}

		public Word[] newArray(int size) {
			return new Word[size];
		}
	};

}

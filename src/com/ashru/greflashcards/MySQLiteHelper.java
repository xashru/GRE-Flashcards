package com.ashru.greflashcards;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

	/** Database Version */
	private static final int DATABASE_VERSION = 1;
	/** Database Name */
	private static final String DATABASE_NAME = "GRE_WORD_DATABASE";

	/** Words data table name */
	private static final String TABLE_WORDS = "words";

	// Word Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_WORD = "word";
	private static final String KEY_MEANING = "meaning";
	private static final String KEY_EXAMPLE = "example";
	private static final String KEY_NOTE = "note";
	private static final String KEY_TIME = "time";
	private static final String KEY_RATING = "rating";
	private static final String KEY_LEARNED = "learned";

	private static final String[] COLUMNS = { KEY_ID, KEY_WORD, KEY_MEANING,
			KEY_EXAMPLE, KEY_NOTE, KEY_TIME, KEY_RATING, KEY_LEARNED };

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create words table
		String CREATE_WORD_TABLE = "CREATE TABLE words ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "word TEXT, "
				+ "meaning TEXT," + "example TEXT," + "note TEXT,"
				+ "time LONG," + "rating INTEGER," + "learned INTEGER)";

		// create words table
		db.execSQL(CREATE_WORD_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older words table if existed
		db.execSQL("DROP TABLE IF EXISTS words");

		// create fresh words table
		this.onCreate(db);
	}

	public void addWord(Word word) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_WORD, word.getWord());
		values.put(KEY_MEANING, word.getWordMeaning());
		values.put(KEY_EXAMPLE, word.getWordExample());
		values.put(KEY_NOTE, word.getWordNote());
		values.put(KEY_TIME, word.getLastStudyTime());
		values.put(KEY_RATING, word.getRating());
		values.put(KEY_LEARNED, word.getLearned());

		// 3. insert
		db.insert(TABLE_WORDS, null, values);

		// 4. close
		db.close();

	}

	public Word getWord(int id) {

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = db.query(TABLE_WORDS, // a. table
				COLUMNS, // b. column names
				" id = ?", // c. selections
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build word object
		Word word = new Word();
		word.setId(Integer.parseInt(cursor.getString(0)));
		word.setWord(cursor.getString(1));
		word.setWordMeaning(cursor.getString(2));
		word.setWordExample(cursor.getString(3));
		word.setWordNote(cursor.getString(4));
		word.setLastStudyTime(cursor.getLong(5));
		word.setRating(cursor.getInt(6));
		word.setLearned(cursor.getInt(7));

		// 5. return word
		return word;
	}

	/**
	 * get all words from database
	 * 
	 * @return an Arraylist containing all words
	 */
	public ArrayList<Word> getAllWords() {
		ArrayList<Word> wordList = new ArrayList<Word>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_WORDS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build word and add it to list
		Word word = null;
		if (cursor.moveToFirst()) {
			do {
				word = getWordFromCursor(cursor);
				wordList.add(word);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return wordList;
	}

	/**
	 * update single word in database
	 * 
	 * @param word
	 *            word to update
	 * @return number of rows affected
	 */
	public int updateWord(Word word) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_WORD, word.getWord());
		values.put(KEY_MEANING, word.getWordMeaning());
		values.put(KEY_EXAMPLE, word.getWordExample());
		values.put(KEY_NOTE, word.getWordNote());
		values.put(KEY_TIME, word.getLastStudyTime());
		values.put(KEY_RATING, word.getRating());
		values.put(KEY_LEARNED, word.getLearned());

		// 3. updating row
		int i = db.update(TABLE_WORDS, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(word.getId()) }); // selection
																// args

		// 4. close
		db.close();

		return i;

	}

	/**
	 * delete single word in database
	 * 
	 * @param book
	 *            word to delete
	 */
	public void deleteBook(Word book) {

		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. delete
		db.delete(TABLE_WORDS, KEY_ID + " = ?",
				new String[] { String.valueOf(book.getId()) });

		// 3. close
		db.close();

	}

	/**
	 * check whether the database is empty
	 * 
	 * @return <code>true</code> if its empty
	 */
	public boolean isEmpty() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORDS, null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.close();
			return false;
		} else {
			cursor.close();
			return true;
		}
	}

	/**
	 * get all matching words from a search query
	 * 
	 * @param search
	 *            search query
	 * @return an ArrayList containing all matching words
	 */
	public ArrayList<Word> getMatchingWords(String search) {

		ArrayList<Word> matchedWords = new ArrayList<Word>();

		// since query returns all words for empty string but I want it to
		// return no word
		if (search.equals("")) {
			return matchedWords;
		}

		SQLiteDatabase db = this.getWritableDatabase();

		try {
			String query = "SELECT  * FROM " + TABLE_WORDS + " WHERE "
					+ KEY_WORD + " LIKE  '" + search + "%'";
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null) {
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					matchedWords.add(getWordFromCursor(cursor));
					cursor.moveToNext();
				}
				cursor.close();
				return matchedWords;
			}
			return matchedWords;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return matchedWords;
	}

	/**
	 * get all words from database
	 * 
	 * @return an Arraylist containing all words
	 */
	public void resetWords() {
		String query = "SELECT  * FROM " + TABLE_WORDS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Word word = null;
		ContentValues values = new ContentValues();

		if (cursor.moveToFirst()) {
			do {
				word = getWordFromCursor(cursor);
				if (word.getLearned() == 0) {
					continue;
				}
				values.put(KEY_WORD, word.getWord());
				values.put(KEY_MEANING, word.getWordMeaning());
				values.put(KEY_EXAMPLE, word.getWordExample());
				values.put(KEY_NOTE, word.getWordNote());
				values.put(KEY_TIME, word.getLastStudyTime());
				values.put(KEY_RATING, word.getRating());
				values.put(KEY_LEARNED, 0);
				db.update(TABLE_WORDS, // table
						values, // column/value
						KEY_ID + " = ?", // selections
						new String[] { String.valueOf(word.getId()) }); // selection
																		// args
			} while (cursor.moveToNext());
		}
	}

	/**
	 * gets the word from a cursor position
	 * 
	 * @param cursor
	 *            cursor
	 * @return the word in the cursor position
	 */
	public Word getWordFromCursor(Cursor cursor) {
		Word word = new Word();
		word.setId(Integer.parseInt(cursor.getString(0)));
		word.setWord(cursor.getString(1));
		word.setWordMeaning(cursor.getString(2));
		word.setWordExample(cursor.getString(3));
		word.setWordNote(cursor.getString(4));
		word.setLastStudyTime(cursor.getLong(5));
		word.setRating(cursor.getInt(6));
		word.setLearned(cursor.getInt(7));
		return word;
	}

}

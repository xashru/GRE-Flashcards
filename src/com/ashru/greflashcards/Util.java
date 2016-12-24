package com.ashru.greflashcards;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class Util {

	/**
	 * a workaround to make text italic in samsung devices
	 * 
	 * @param text
	 *            text of the view
	 * @return italic text
	 */
	public static CharSequence setTextStyleItalic(CharSequence text) {
		final StyleSpan style = new StyleSpan(Typeface.ITALIC);
		if (!text.equals("")) {
			// add space to avoid clipping
			text = text.toString() + " ";
		}
		final SpannableString str = new SpannableString(text);
		str.setSpan(style, 0, text.length(), 0);
		return str;
	}

}

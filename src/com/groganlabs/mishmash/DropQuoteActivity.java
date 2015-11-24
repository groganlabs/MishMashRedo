package com.groganlabs.mishmash;

import android.app.Activity;
import android.os.Bundle;

public class DropQuoteActivity extends Activity {
	DropQuoteGame mGame;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropquotes);
	}
	
	public DropQuoteGame getGame() {
		return mGame;
	}
}

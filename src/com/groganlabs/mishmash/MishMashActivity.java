package com.groganlabs.mishmash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MishMashActivity extends Activity implements OnClickListener {
	
	TextView cryptogram;
	TextView dropQuotes;
	TextView jumble;
	TextView settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        cryptogram = (TextView) findViewById(R.id.cryptoBtn);
        dropQuotes = (TextView) findViewById(R.id.dropBtn);
        jumble = (TextView) findViewById(R.id.jumbleBtn);
        settings = (TextView) findViewById(R.id.settingsBtn);
        
        cryptogram.setOnClickListener(this);
        dropQuotes.setOnClickListener(this);
        jumble.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent i;
		if(v.getId() == R.id.cryptoBtn) {
			i = new Intent(this, CryptogramActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.dropBtn) {
			i = new Intent(this, DropQuoteActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.jumbleBtn) {
			i = new Intent(this, JumbleActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.settingsBtn) {
			i = new Intent(this, MishMashSettings.class);
			startActivity(i);
		}
	}
}
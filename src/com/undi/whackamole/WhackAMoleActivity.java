package com.undi.whackamole;

import com.undi.whackamole.audio.WhackAMoleAudio;
import com.undi.whackamole.view.WhackAMoleView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity;

public class WhackAMoleActivity extends Activity {
	
	private static final int TOGGLE_SOUND = 1;
	
	private WhackAMoleView view;
	private WhackAMoleAudio audio;

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_whack_amole);
		view = (WhackAMoleView) findViewById(R.id.mole);
		view.setKeepScreenOn(true);
		audio = WhackAMoleAudio.getInstance();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem toggleSound = menu.add(Menu.NONE, TOGGLE_SOUND, 0, "Toggle Sound");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case TOGGLE_SOUND:
			String soundStatus = audio.toggleSound() ? "On" : "Off";
			Toast.makeText(this, "Sound " + soundStatus, Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

}

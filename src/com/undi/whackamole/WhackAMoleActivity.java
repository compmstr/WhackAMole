package com.undi.whackamole;

import com.undi.whackamole.view.WhackAMoleView;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;

public class WhackAMoleActivity extends Activity {
	
	private WhackAMoleView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_whack_amole);
		view = (WhackAMoleView) findViewById(R.id.mole);
		view.setKeepScreenOn(true);
	}

}

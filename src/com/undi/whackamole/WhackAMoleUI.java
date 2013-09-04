package com.undi.whackamole;

import com.undi.android.ui.UIButton;
import com.undi.android.ui.UICallback;
import com.undi.whackamole.audio.WhackAMoleAudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class WhackAMoleUI {
	private SurfaceHolder surfaceHolder;
	private Bitmap backgroundImg;
	private Context context;
	private boolean onTitle = true;
	private int screenW = 1;
	private int screenH = 1;

	private UIButton soundOnButton;
	private UIButton soundOffButton;
	private UIButton curSoundButton;
	private static WhackAMoleAudio audio;

	private static final UICallback soundToggleCallback = new UICallback() {
		@Override
		public void run(Object... args) {
			audio.toggleSound();
		}
	};
	
	private void updateSoundButton(){
		curSoundButton = audio.isSoundEnabled() ? soundOffButton : soundOnButton;
	}

	public WhackAMoleUI(SurfaceHolder holder, Context context,
			Handler handler) {
		this.context = context;
		this.surfaceHolder = holder;
		this.backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.title);
		if(audio == null){
			audio = WhackAMoleAudio.getInstance();
		}
		Bitmap rawGraphic = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sound_on_button);
		soundOnButton = new UIButton(rawGraphic, 5, 5, 5, screenW, screenH, soundToggleCallback);
		rawGraphic = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sound_off_button);
		soundOffButton = new UIButton(rawGraphic, 5, 5, 5, screenW, screenH, soundToggleCallback);
		updateSoundButton();
	}

	public void draw(Canvas canvas){
		try{
			canvas.drawBitmap(backgroundImg, 0, 0, null);
			updateSoundButton();
			if(curSoundButton != null){
				curSoundButton.draw(canvas);
			}
		}catch(Exception e){
		}
	}
	
	public SurfaceHolder getSurfaceHolder() { return surfaceHolder; }
	
	public boolean doTouchEvent(MotionEvent event){
		synchronized(surfaceHolder){
			int eventAction = event.getAction();
			int x = (int) event.getX();
			int y = (int) event.getY();
			updateSoundButton();
			
			switch(eventAction){
			case MotionEvent.ACTION_DOWN:
				if(curSoundButton != null){
					curSoundButton.checkPressed(x, y);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if(curSoundButton != null){
					curSoundButton.checkReleased(x, y);
				}
				if(onTitle){
					backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
					backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
					onTitle = false;
				}
				break;
			}
		}
		return true;
	}
	
	public void setSurfaceSize(int width, int height){
		synchronized(surfaceHolder){
			screenW = width;
			screenH = height;
			backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
		}
	}
	
}

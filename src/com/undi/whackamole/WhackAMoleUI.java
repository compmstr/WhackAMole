package com.undi.whackamole;

import java.util.Random;

import com.undi.android.ui.UIButton;
import com.undi.android.ui.UICallback;
import com.undi.whackamole.audio.WhackAMoleAudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class WhackAMoleUI {
	private SurfaceHolder surfaceHolder;
	private Bitmap backgroundImg;
	private Context context;
	private boolean onTitle = true;
	private int screenW = 1;
	private int screenH = 1;
	private int bgOrigW, bgOrigH;
	private float scaleW, scaleH;

	private UIButton soundOnButton;
	private UIButton soundOffButton;
	private UIButton curSoundButton;
	private static WhackAMoleAudio audio;
	private static Options imgLoadOptions = new Options();
	
	private Mole[] moles;
	private int molesWhacked = 0;
	private int molesMissed = 0;

	private final UICallback soundToggleCallback = new UICallback() {
		@Override
		public void run(Object... args) {
			String soundStatus = audio.toggleSound() ? "On" : "Off";
			Toast.makeText(context, "Sound " + soundStatus, Toast.LENGTH_SHORT).show();
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
		imgLoadOptions.inScaled = false;
		Bitmap rawGraphic = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sound_on_button, imgLoadOptions);
		soundOnButton = new UIButton(rawGraphic, 0.01, 0.01, 0.05, screenW, screenH, soundToggleCallback);
		rawGraphic = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sound_off_button, imgLoadOptions);
		soundOffButton = new UIButton(rawGraphic, 0.01, 0.01, 0.05, screenW, screenH, soundToggleCallback);
		updateSoundButton();
	}
	
	private boolean anyMolesMoving(){
		for(Mole mole: moles){
			if(mole.isMoving()){
				return true;
			}
		}
		return false;
	}
	
	private void pickMole(){
		moles[new Random().nextInt(7)].popUp();;
	}

	public void draw(Canvas canvas){
		try{
			canvas.drawBitmap(backgroundImg, 0, 0, null);
			updateSoundButton();
			if(curSoundButton != null){
				curSoundButton.draw(canvas);
			}
			if(!onTitle){
				if(!anyMolesMoving()){
					pickMole();
				}
				long curTime = System.currentTimeMillis();
				try{
					for(Mole mole : moles){
						mole.draw(canvas);
						mole.update(curTime);
					}
				}catch(Exception e){
					
				}
			}
		}catch(Exception e){
		}
	}
	
	public SurfaceHolder getSurfaceHolder() { return surfaceHolder; }
	
	private void loadMoles(){
		Mole.loadGraphics(context.getResources(), scaleW, scaleH);
		moles = new Mole[7];
		moles[0] = new Mole(55, 475);
		moles[1] = new Mole(155, 425);
		moles[2] = new Mole(255, 475);
		moles[3] = new Mole(355, 425);
		moles[4] = new Mole(455, 475);
		moles[5] = new Mole(555, 425);
		moles[6] = new Mole(655, 475);
	}
	
	private void updateMissed(){
		if(moles != null){
			for(Mole mole : moles){
				if(mole.isMissed()){
					molesMissed++;
					mole.clearMissed();
				}
			}
		}
	}
	
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
					if(!onTitle && moles != null){
						for(Mole mole : moles){
							if(mole.checkHit(x, y)){
								//TODO: process whack
								molesWhacked++;
								if(molesWhacked % 10 == 0){
									Mole.setMoveSpeed((int) (Mole.getMoveSpeed() * 1.1));
								}
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if(curSoundButton != null){
					curSoundButton.checkReleased(x, y);
				}
				if(onTitle){
					backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.background, imgLoadOptions);
					bgOrigW = backgroundImg.getWidth();
					bgOrigH = backgroundImg.getHeight();
					scaleW = (float) screenW / (float) bgOrigW;
					scaleH = (float) screenH / (float) bgOrigH;
					backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
					loadMoles();
					onTitle = false;
				}else{
					for(Mole mole: moles){
						mole.clearWhacked();
					}
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
			soundOffButton.rescaleGraphic(width, height);
			soundOnButton.rescaleGraphic(width, height);
		}
	}
	
}

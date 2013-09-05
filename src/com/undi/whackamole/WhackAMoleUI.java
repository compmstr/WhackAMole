package com.undi.whackamole;

import com.undi.android.ui.UIButton;
import com.undi.android.ui.UICallback;
import com.undi.android.ui.UIPushButton;
import com.undi.android.ui.UIToggleButton;
import com.undi.whackamole.Game.State;
import com.undi.whackamole.audio.WhackAMoleAudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class WhackAMoleUI {
	private SurfaceHolder surfaceHolder;
	private Bitmap backgroundImg;
	private Bitmap gameOverImg;
	private Context context;
	private int screenW = 1;
	private int screenH = 1;
	private int bgOrigW, bgOrigH;
	private float scaleW, scaleH;
	private Game game;

	private UIButton soundToggleButton;
	private static WhackAMoleAudio audio;
	private static Options imgLoadOptions = new Options();
	
	private Paint blackPaint;
	private Paint blackCenterPaint;

	private final UICallback soundToggleCallback = new UICallback() {
		@Override
		public void run(Object... args) {
			String soundStatus = audio.toggleSound() ? "On" : "Off";
			Toast.makeText(context, "Sound " + soundStatus, Toast.LENGTH_SHORT).show();
		}
	};
	
	public WhackAMoleUI(SurfaceHolder holder, Context context,
			Handler handler) {
		this.context = context;
		this.surfaceHolder = holder;
		this.backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.title);
		if(audio == null){
			audio = WhackAMoleAudio.getInstance();
			audio.loadSounds(context);
		}
		blackPaint = new Paint();
		blackPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		blackPaint.setStyle(Style.STROKE);
		blackPaint.setTextAlign(Align.LEFT);
		blackCenterPaint = new Paint();
		blackCenterPaint.setAntiAlias(true);
		blackCenterPaint.setColor(Color.BLACK);
		blackCenterPaint.setStyle(Style.STROKE);
		blackCenterPaint.setTextAlign(Align.CENTER);

		imgLoadOptions.inScaled = false;
		Bitmap rawGraphic = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.sound_toggle_button, imgLoadOptions);
		soundToggleButton = new UIToggleButton(rawGraphic, 0.01, 0.88, 0.05, screenW, screenH, soundToggleCallback);
		
		game = new Game();
		
	}
	
	public void draw(Canvas canvas){
		try{
			canvas.drawBitmap(backgroundImg, 0, 0, null);
			if(soundToggleButton != null){
				soundToggleButton.draw(canvas);
			}
			switch(game.getState()){
			case COUNTDOWN:
				float countdownRemaining = game.getCountdownRemaining();
				if(countdownRemaining > 0.0f){
					canvas.drawText(String.format("Starting in: %d", (int)countdownRemaining), screenW / 2, screenH / 2, blackCenterPaint);
				}else{
					game.startGame();
				}
				break;
			case PLAYING:
				if(!game.anyMolesMoving()){
					game.pickMole();
				}
				long curTime = System.currentTimeMillis();
				try{
					for(Mole mole : game.getMoles()){
						mole.draw(canvas);
						mole.update(curTime);
					}
					if(game.updateMissed()){ audio.playMiss(); }
				}catch(Exception e){

				}
				canvas.drawText("Whacked: " + Integer.toString(game.getMolesWhacked()), 10, blackPaint.getTextSize() + 10, blackPaint);
				canvas.drawText("Missed: " + Integer.toString(game.getMolesMissed()), screenW - (int)(200 * scaleW), blackPaint.getTextSize() + 10, blackPaint);

				if(game.checkGameOver()){
					if(gameOverImg == null){
						gameOverImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover);
						gameOverImg = Bitmap.createScaledBitmap(gameOverImg, (int)(gameOverImg.getWidth() * scaleW),
								(int)(gameOverImg.getHeight() * scaleH), true);
					}
				}
				break;
			case GAME_OVER:
				canvas.drawBitmap(gameOverImg, (screenW / 2) - (gameOverImg.getWidth() / 2),
						(screenH / 2) - (gameOverImg.getHeight() / 2), null);
				break;
			case TITLE:
				break;
			}
		}catch(Exception e){
		}
	}
	
	public SurfaceHolder getSurfaceHolder() { return surfaceHolder; }
	
	private void loadMoles(){
		Mole.loadGraphics(context.getResources(), scaleW, scaleH);
		game.generateMoles();
	}
	
	public boolean doTouchEvent(MotionEvent event){
		synchronized(surfaceHolder){
			int eventAction = event.getAction();
			int x = (int) event.getX();
			int y = (int) event.getY();
			boolean buttonEvent = false;
			
			switch(eventAction){
			case MotionEvent.ACTION_DOWN:
				if(soundToggleButton != null){
					if(soundToggleButton.checkPressed(x, y)){
						buttonEvent = true;
					}
					if(!buttonEvent){
						if(game.getState() == State.PLAYING){
							if(game.checkWhacks(x, y)){
								audio.playWhack();
							}
						}
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if(soundToggleButton != null){
					if(soundToggleButton.checkReleased(x, y)){
						buttonEvent = true;
					}
				}
				if(!buttonEvent){
					switch(game.getState()){
					case TITLE:
						backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.background, imgLoadOptions);
						updateScale();
						backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
						loadMoles();
						game.startCountdown();
						break;
					case PLAYING:
						game.clearWhacked();
						break;
					case GAME_OVER:
						game.startCountdown();
						break;
					default:
						break;
					}
				}
				break;
			}
		}
		return true;
	}
	
	private void updateScale(){
		bgOrigW = backgroundImg.getWidth();
		bgOrigH = backgroundImg.getHeight();
		scaleW = (float) screenW / (float) bgOrigW;
		scaleH = (float) screenH / (float) bgOrigH;
		blackPaint.setTextSize(scaleW * 30);
		blackCenterPaint.setTextSize(scaleW * 40);
	}
	
	public void setSurfaceSize(int width, int height){
		synchronized(surfaceHolder){
			screenW = width;
			screenH = height;
			updateScale();
			backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
			soundToggleButton.rescaleGraphic(width, height);
		}
	}
	
}

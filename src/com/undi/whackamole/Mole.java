package com.undi.whackamole;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;

public class Mole {
	private final static float DEFAULT_CYCLE_TIME = 2.0f;
	private static Bitmap rawMole, mole;
	private static Bitmap rawMask, mask;
	private static Bitmap rawWhack, whack;
	private static float scaleW, scaleH;
	
	//scaled coordinates to draw at
	private int moleX, moleY;
	private int maskX, maskY;
	//unscaled offset (scaled when drawing)
	private float moleOffset = 0f;
	private boolean rising, sinking, whacked, missed;
	private long lastTime = -1;
	private static int moveSpeed;
	
	public static void loadGraphics(Resources res, float scaleW, float scaleH){
		Options opts = new Options();
		opts.inScaled = false;
		Mole.rawMole = BitmapFactory.decodeResource(res, R.drawable.mole, opts);
		Mole.rawMask = BitmapFactory.decodeResource(res, R.drawable.mask, opts);
		Mole.rawWhack = BitmapFactory.decodeResource(res, R.drawable.whack, opts);

		rescaleGraphics(scaleW, scaleH);
	}
	
	private static void rescaleGraphics(float scaleW, float scaleH){
		if(rawMole == null || rawMask == null){
			return;
		}
		Mole.scaleW = scaleW;
		Mole.scaleH = scaleH;

		mole = Bitmap.createScaledBitmap(rawMole, (int)(rawMole.getWidth() * scaleW),
				(int)(rawMole.getHeight() * scaleH), true);
		mask = Bitmap.createScaledBitmap(rawMask, (int)(rawMask.getWidth() * scaleW),
				(int)(rawMask.getHeight() * scaleH), true);
		whack = Bitmap.createScaledBitmap(rawWhack, (int)(rawWhack.getWidth() * scaleW),
				(int)(rawWhack.getHeight() * scaleH), true);
	}
	
	public Mole(int x, int y){
		this.moleX = (int) (x * scaleW);
		this.moleY = (int) (y * scaleH);
		this.maskX = (int) ((x - 5) * scaleW);
		this.maskY = (int) ((y - 25) * scaleH);
		
		rising = sinking = whacked = missed = false;
		resetCycleTime();
	}
	
	public boolean checkHit(int x, int y){
		if(x > moleX &&
			x <= moleX + mole.getWidth() &&
			y > moleY - moleOffset &&
			y <= moleY){
			whacked = true;
			return true;
		}
		return false;
	}
	public static void resetCycleTime(){
		//Set the cycle time in seconds
		setCycleTime(DEFAULT_CYCLE_TIME);
	}
	public void reset(){
		whacked = false;
		rising = sinking = whacked = missed = false;
		moleOffset = 0;
		lastTime = -1;
	}
	/**
	 * Resets the mole if currently whacked
	 */
	public void clearWhacked(){
		if(whacked){
			reset();
		}
	}
	
	/**
	 * Converts the passed in cycle type to a move speed
	 * @param cycleTime
	 * @return
	 */
	public static int cycleTimeMoveSpeed(float cycleTime){
		return (int) ((175 * 2) / cycleTime);
	}
	public static float moveSpeedCycleTime(int moveSpeed){
		return (175 * 2.0f) / (float) moveSpeed;
	}
	
	public void update(long curTime){
		if(lastTime == -1){ lastTime = curTime; }
		float deltaTime = (curTime - lastTime) / 1000.0f;
		if(!whacked){
			if(rising){
				moleOffset += (moveSpeed * deltaTime);
				if(moleOffset >= 175){
					moleOffset = 175;
					rising = !rising;
					sinking = !sinking;
				}
			}else if(sinking){
				moleOffset -= (moveSpeed * deltaTime);
				if(moleOffset <= 0){
					moleOffset = 0;
					sinking = !sinking;
					missed = true;
				}
			}
		}
		lastTime = curTime;
	}
	
	public void draw(Canvas canvas){
		canvas.drawBitmap(mole, moleX, moleY - (moleOffset * scaleH), null);
		canvas.drawBitmap(mask, maskX, maskY, null);
		if(whacked){
			int drawX = (moleX + (mole.getWidth() / 2)) - (whack.getWidth() / 2);
			canvas.drawBitmap(whack, drawX, moleY - moleOffset, null);
		}
	}
	
	public static void setCycleTime(float cycleTime){ moveSpeed = cycleTimeMoveSpeed(cycleTime); }
	public static float getCycleTime(){ return moveSpeedCycleTime(moveSpeed); }
	public static void setMoveSpeed(int newSpeed){ moveSpeed = newSpeed; }
	public static int getMoveSpeed(){ return moveSpeed; }
	public void popUp(){ this.rising = true; }
	public boolean isMoving(){ return (rising || sinking); }
	public boolean isMissed(){ return missed; }
	public void clearMissed(){ missed = false; }
}

package com.undi.android.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * UI Button class...
 * Expects graphic to be an image that has two panels on top of each other, the top one as the
 *   base state, and the bottom one as pressed.
 * @author corey
 *
 */
public class UIButton {
	private Bitmap rawGraphic;
	private Bitmap graphic;
	private Rect pos = new Rect();
	private UICallback callback;
	private Rect srcRect = new Rect();
	private boolean isPressed;
	private double xPercent, yPercent, wPercent;
	
	private void init(){
		scaleGraphic();
		isPressed = false;
	}
	
	public UIButton(Bitmap graphic, double xPercent, double yPercent, double wPercent,
			int screenW, int screenH,
			UICallback callback){
		rawGraphic = graphic;
		this.xPercent = xPercent;
		this.yPercent = yPercent;
		this.wPercent = wPercent;
		rescaleGraphic(screenW, screenH);
		this.callback = callback;
		init();
	}
	
	public UIButton rescaleGraphic(int screenW, int screenH){
		int left = (int) (xPercent * screenW);
		int top = (int) (yPercent * screenH);
		int right = (int) (left + (wPercent * screenW));
		int height = (int) ((right - left) * ((rawGraphic.getHeight() / 2.0) / rawGraphic.getWidth()));
		int bottom = (int) (top + height);
		pos.left = left;
		pos.top = top;
		pos.right = right;
		pos.bottom = bottom;
		scaleGraphic();
		
		return this;
	}
	
	private void scaleGraphic(){
		scaleGraphic(pos.right - pos.left, 2 * (pos.bottom - pos.top));
	}
	private void scaleGraphic(int w, int h){
		if(rawGraphic != null && w > 0 && h > 0){
			graphic = Bitmap.createScaledBitmap(rawGraphic, w, h, false);
			srcRect.left = 0;
			srcRect.top = 0;
			srcRect.right = this.graphic.getWidth();
			srcRect.bottom = this.graphic.getHeight() / 2;
			//update the top/bottom of srcRect using isPressed, if we're supposed
			//  to be in the pressed state
			if(isPressed){
				setPressed(isPressed);
			}
		}
	}
	
	public void onClick(Object...args){
		if(callback != null){
			if(args != null && args.length != 0){
				callback.run(args);
			}else{
				callback.run();
			}
		}
	}
	
	public UIButton setPressed(boolean pressed){
		isPressed = pressed;
		int halfHeight = graphic.getHeight() / 2;
		if(isPressed){
			srcRect.top = halfHeight;
			srcRect.bottom = graphic.getHeight();
		}else{
			srcRect.top = 0;
			srcRect.bottom = halfHeight;
		}
		return this;
	}
	
	public UIButton draw(Canvas canvas){
		if(graphic != null){
			canvas.drawBitmap(graphic, srcRect, pos, null);
		}

		return this;
	}
	
	public boolean contains(int x, int y){
		return pos.contains(x, y);
	}
	
	public boolean checkPressed(int x, int y){
		if(contains(x, y)){
			setPressed(true);
			return true;
		}else{
			setPressed(false);
			return false;
		}
	}
	public boolean checkReleased(int x, int y, Object...callbackArgs){
		setPressed(false);
		if(contains(x, y)){
			onClick(callbackArgs);
			return true;
		}else{
			return false;
		}
	}
}

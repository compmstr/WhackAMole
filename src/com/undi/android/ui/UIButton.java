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
public abstract class UIButton {
	protected Bitmap rawGraphic;
	protected Bitmap graphic;
	protected UICallback callback;
	protected Rect srcRect = new Rect();
	protected Rect pos = new Rect();
	protected boolean isPressed = false;
	protected double xPercent, yPercent, wPercent;
	//The number of states this button has
	//  ex: regular push button has one 'state'
	//      toggle button has two 'states'
	protected int numStates = 1;
	protected int state = 0;
	
	/**
	 * Constructor for use by derived classes
	 */
	protected UIButton(){}
	
	public UIButton(Bitmap graphic, double xPercent, double yPercent, double wPercent,
			int screenW, int screenH,
			UICallback callback){
		rawGraphic = graphic;
		this.xPercent = xPercent;
		this.yPercent = yPercent;
		this.wPercent = wPercent;
		this.callback = callback;
	}
	
	public UIButton rescaleGraphic(int screenW, int screenH){
		int left = (int) (xPercent * screenW);
		int top = (int) (yPercent * screenH);
		int right = (int) (left + (wPercent * screenW));
		float aspectRatio =  ((rawGraphic.getHeight() / 2.0f) / 
					(rawGraphic.getWidth() / (numStates == 0 ? 1 : numStates)));
		int height = (int) ((right - left) * aspectRatio);
		int bottom = (int) (top + height);
		pos.left = left;
		pos.top = top;
		pos.right = right;
		pos.bottom = bottom;
		scaleGraphic();
		
		return this;
	}
	
	private void scaleGraphic(){
		scaleGraphic(pos.right - pos.left, pos.bottom - pos.top);
	}
	private void scaleGraphic(int w, int h){
		if(rawGraphic != null && w > 0 && h > 0){
			graphic = Bitmap.createScaledBitmap(rawGraphic, w * numStates, h * 2, false);
			srcRect.left = 0;
			srcRect.top = 0;
			srcRect.right = w;
			srcRect.bottom = h;
			//update the top/bottom of srcRect using isPressed, if we're supposed
			//  to be in the pressed state
			if(isPressed){
				setPressed(isPressed);
			}
			if(state != 0){
				setState(state);
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
	
	public UIButton setState(int newState){
		if(newState <= numStates){
			state = newState;
			int stateWidth = graphic.getWidth() / numStates;
			srcRect.left = (state * stateWidth);
			srcRect.right = ((state + 1) * stateWidth);
		}else{
			throw new IllegalArgumentException("Invalid state sent to button");
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
			cycleState();
			return true;
		}else{
			return false;
		}
	}
	
	private void cycleState(){
		setState((state + 1) % numStates);
	}
	
	public int getState(){ return state; }
}

package com.undi.whackamole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class WhackAMoleThread extends Thread{
	private SurfaceHolder surfaceHolder;
	private Bitmap backgroundImg;
	private Context context;
	private boolean running = false;
	private boolean onTitle = true;
	private int screenW = 1;
	private int screenH = 1;

	public WhackAMoleThread(SurfaceHolder holder, Context context,
			Handler handler) {
		this.context = context;
		this.surfaceHolder = holder;
		this.backgroundImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.title);
	}
	@Override
	public void run(){
		while(running){
			Canvas c = null;
			try{
				c = surfaceHolder.lockCanvas();
				synchronized(surfaceHolder){
					draw(c);
				}
			}finally {
				if(c != null){
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	private void draw(Canvas canvas){
		try{
			canvas.drawBitmap(backgroundImg, 0, 0, null);
		}catch(Exception e){
		}
	}
	
	public boolean doTouchEvent(MotionEvent event){
		synchronized(surfaceHolder){
			int eventAction = event.getAction();
			int x = (int) event.getX();
			int y = (int) event.getY();
			
			switch(eventAction){
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
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
	
	public void setRunning(boolean b){
		running = b;
	}
}
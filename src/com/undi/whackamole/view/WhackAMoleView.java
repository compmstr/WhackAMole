package com.undi.whackamole.view;

import java.lang.Thread.State;

import com.undi.whackamole.WhackAMoleThread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class WhackAMoleView extends SurfaceView implements Callback {
	
	private WhackAMoleThread thread;

	public WhackAMoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread = new WhackAMoleThread(holder, context,
				new Handler(){
			@Override
			public void handleMessage(Message m){
				
			}
		});
		setFocusable(true);;
	}

	public WhackAMoleThread getThread() { return thread; }
	
	@Override
	public boolean onTouchEvent(MotionEvent evt){
		return thread.doTouchEvent(evt);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		if(thread.getState() == State.NEW){
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
	}
	
	

}

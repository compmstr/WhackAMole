package com.undi.whackamole;

import android.graphics.Canvas;

public class WhackAMoleThread extends Thread{
	private boolean running = false;
	private WhackAMoleUI ui;

	public WhackAMoleThread(WhackAMoleUI ui) {
		this.ui = ui;
	}

	@Override
	public void run(){
		while(running){
			Canvas c = null;
			try{
				c = ui.getSurfaceHolder().lockCanvas();
				synchronized(ui.getSurfaceHolder()){
					ui.draw(c);
				}
			}finally {
				if(c != null){
					ui.getSurfaceHolder().unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	public void setRunning(boolean b){
		running = b;
	}
}
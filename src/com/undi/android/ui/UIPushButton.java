package com.undi.android.ui;

import android.graphics.Bitmap;

public class UIPushButton extends UIButton {
	public UIPushButton(Bitmap graphic, double xPercent, double yPercent, double wPercent,
			int screenW, int screenH,
			UICallback callback){
		super(graphic, xPercent, yPercent, wPercent,
					screenW, screenH, callback);
		this.numStates = 1;
		rescaleGraphic(screenW, screenH);
	}
}

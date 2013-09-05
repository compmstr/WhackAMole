package com.undi.android.ui;

import android.graphics.Bitmap;

public class UIToggleButton extends UIButton {

	public UIToggleButton(Bitmap graphic, double xPercent, double yPercent,
			double wPercent, int screenW, int screenH, UICallback callback) {
		super(graphic, xPercent, yPercent, wPercent,
					screenW, screenH, callback);
		this.numStates = 2;
		rescaleGraphic(screenW, screenH);
	}
}

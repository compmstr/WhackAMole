package com.undi.whackamole.audio;

public class WhackAMoleAudio {
	private boolean soundEnabled = true;
	private static volatile WhackAMoleAudio _instance = null;
	
	private WhackAMoleAudio(){
	}
	
	public static synchronized WhackAMoleAudio getInstance(){
		if(_instance == null){
			_instance = new WhackAMoleAudio();
		}
		return _instance;
	}
	
	/**
	 * Toggle the enabled state of sound
	 * @return the new value of soundEnabled
	 */
	public boolean toggleSound(){
		soundEnabled = !soundEnabled;
		return soundEnabled;
	}
	
	public void setSoundEnabled(boolean enabled){ soundEnabled = enabled; }
	public boolean isSoundEnabled(){ return soundEnabled; }
}

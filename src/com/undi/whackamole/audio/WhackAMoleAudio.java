package com.undi.whackamole.audio;

import com.undi.whackamole.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class WhackAMoleAudio {
	private boolean soundEnabled = true;
	private static volatile WhackAMoleAudio _instance = null;
	
	private static SoundPool sounds;
	private static int whackSound;
	private static int missSound;
	private Context context;
	
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
	
	public void loadSounds(Context context){
		this.context = context;
		sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		whackSound = sounds.load(context, R.raw.whack, 1);
		missSound = sounds.load(context, R.raw.miss, 1);
	}
	
	private void playSound(int sound){
		if(context != null && sounds != null && soundEnabled){
			AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			float volume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			sounds.play(sound, volume, volume, 1, 0, 1);
		}
	}
	public void playWhack(){ playSound(whackSound); }
	public void playMiss(){ playSound(missSound); }
	
	public void setSoundEnabled(boolean enabled){ soundEnabled = enabled; }
	public boolean isSoundEnabled(){ return soundEnabled; }
}

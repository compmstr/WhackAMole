package com.undi.whackamole;

import java.util.Random;

public class Game {
	public enum State {TITLE, COUNTDOWN, PLAYING, GAME_OVER};
	
	private State state = State.TITLE;
	private Mole[] moles;
	private int molesWhacked = 0;
	private int molesMissed = 0;
	
	private long countdownEndTime = -1;
	private static final int COUNTDOWN_DURATION = 4000;
	

	public boolean anyMolesMoving(){
		for(Mole mole: moles){
			if(mole.isMoving()){
				return true;
			}
		}
		return false;
	}
	
	public void startCountdown(){
		countdownEndTime = System.currentTimeMillis() + COUNTDOWN_DURATION;
		setState(State.COUNTDOWN);
	}
	
	public float getCountdownRemaining(){
		long curTime = System.currentTimeMillis();
		return (countdownEndTime - curTime) / 1000.0f;
	}
	
	public void pickMole(){
		moles[new Random().nextInt(7)].popUp();;
	}
	
	public void generateMoles(){
		moles = new Mole[7];
		moles[0] = new Mole(55, 475);
		moles[1] = new Mole(155, 425);
		moles[2] = new Mole(255, 475);
		moles[3] = new Mole(355, 425);
		moles[4] = new Mole(455, 475);
		moles[5] = new Mole(555, 425);
		moles[6] = new Mole(655, 475);
	}

	public boolean updateMissed(){
		boolean missed = false;
		if(moles != null){
			for(Mole mole : moles){
				if(mole.isMissed()){
					molesMissed++;
					mole.clearMissed();
					missed = true;
				}
			}
		}
		return missed;
	}

	public boolean checkWhacks(int x, int y){
		boolean whacked = false;
		if(moles != null){
			for(Mole mole : moles){
				if(mole.checkHit(x, y)){
					molesWhacked++;
					if(molesWhacked % 10 == 0){
						Mole.setMoveSpeed((int) (Mole.getMoveSpeed() * 1.1));
					}
					whacked = true;
				}
			}
		}
		return whacked;
	}
	
	public void clearWhacked(){
		if(moles != null){
			for(Mole mole: moles){
				mole.clearWhacked();
			}
		}
	}
	
	public void resetMoles(){
		if(moles != null){
			for(Mole mole: moles){
				mole.reset();
			}
		}
	}
	
	public boolean checkGameOver(){
		if(molesMissed >= 5){
			state = State.GAME_OVER;
		}
		return state == State.GAME_OVER;
	}
	
	
	public Mole[] getMoles(){ return moles; }
	public int getMolesWhacked(){ return molesWhacked; }
	public int getMolesMissed(){ return molesMissed; }

	public State getState(){ return state; }
	public void setState(State newState){ state = newState; }
	
	public void startGame(){
		molesWhacked = molesMissed = 0;
		resetMoles();
		Mole.resetCycleTime();
		setState(State.PLAYING);
	}
}

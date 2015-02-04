package com.onettm.ak74;

import java.util.TimerTask;

public class AKTimerTask extends TimerTask {

    private final MainActivity.GameFragment gameFragment;

    public AKTimerTask(MainActivity.GameFragment gameFragment) {
        super();
        this.gameFragment = gameFragment;
    }


    @Override
    public void run() {
        gameFragment.incrementTick();
    }

}

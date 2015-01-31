package com.onettm.ak74;

import java.util.TimerTask;

public class AKTimerTask extends TimerTask {

    private final MainActivity context;
    private MainActivity.PlaceholderFragment fragment;


    public AKTimerTask(MainActivity context, MainActivity.PlaceholderFragment fragment) {
        super();
        this.context = context;
        this.fragment = fragment;
    }



    @Override
    public void run() {
        fragment.getHandler().post(new Runnable() {
            @Override
            public void run() {
                int currentTick = context.getLastTick()-1;
                context.setLastTick(currentTick);
                fragment.getTextView().setText(currentTick + "");
                if (context.getCurrentNumber()==MainActivity.MAX_NUMBER){
                    context.setLastTick(0);
                    cancel();
                    context.showWin();
                }else{
                    if(currentTick <= 0) {
                        cancel();
                        context.showFail();
                    }
                }
            }
        });
    }

}

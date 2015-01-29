package com.onettm.ak74;

import android.widget.Toast;

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
                int currentTick = context.getMaxTick()-1;
                context.setMaxTick(currentTick);
                fragment.getTextView().setText(currentTick + "");
                if(currentTick <= 0) {
                    cancel();
                    context.showFail();
                }
            }
        });
    }

}

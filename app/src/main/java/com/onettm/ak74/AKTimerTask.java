package com.onettm.ak74;

import android.widget.Toast;

import java.util.TimerTask;

public class AKTimerTask extends TimerTask {

    private final MainActivity context;
    private MainActivity.PlaceholderFragment fragment;
    private int maxTick;

    public AKTimerTask(MainActivity context, MainActivity.PlaceholderFragment fragment, int maxTick) {
        super();
        this.context = context;
        this.fragment = fragment;
        this.maxTick = maxTick;
    }

    @Override
    public void run() {
        fragment.getHandler().post(new Runnable() {
            @Override
            public void run() {
                maxTick--;
                fragment.getTextView().setText(maxTick + "");
                if(maxTick <= 0) {
                    cancel();
                    Toast.makeText(context, "THE END", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

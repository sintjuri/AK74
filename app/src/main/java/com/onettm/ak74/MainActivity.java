package com.onettm.ak74;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements ListDialog.Callbacks {

    private static int level = 0;
    private static int[] delays = {5, 7, 10, 25};
    private int maxTick;
    private Timer akTimer;

    public int getMaxTick() {
        return maxTick;
    }

    public void setMaxTick(int maxTick) {
        this.maxTick = maxTick;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onItemSelected(int level) {
        this.level = level;
        startTimer();
    }

    private void startTimer() {
        PlaceholderFragment fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.placeholderFragment);
        akTimer = new Timer();
        if (maxTick <= 0) {
            maxTick = delays[level];
        }
        akTimer.schedule(new AKTimerTask(this, fragment), 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(akTimer!=null) {
            akTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((akTimer!=null) && (maxTick>0)){
            startTimer();
        }
    }

    private void showAlert(String title, String message, int drawable) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(drawable);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openDialog();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected void openDialog() {
        ListDialog listDialog = new ListDialog();
        FragmentManager fm = getFragmentManager();
        listDialog.show(fm, "list_dialog");
    }

    public void showWin(){
        showAlert(getString(R.string.winTitle), getString(R.string.winMessage), R.drawable.win);
    }

    public void showFail(){
        showAlert(getString(R.string.failTitle), getString(R.string.failMessage), R.drawable.fail);
    }

    public static class PlaceholderFragment extends Fragment {

        private Handler handler;
        private TextView timerText;

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);

            ((MainActivity)getActivity()).openDialog();

            handler = new Handler();
            timerText = (TextView) rootView.findViewById(R.id.timerText);

            return rootView;
        }

        public Handler getHandler() {
            return handler;
        }

        public TextView getTextView() {
            return timerText;
        }


    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("74760DA0E4A7D8383E8EC5268A2486CF")
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /**
         * Called when leaving the activity
         */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /**
         * Called when returning to the activity
         */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /**
         * Called before the activity is destroyed
         */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

        public class AKTimerTask extends TimerTask {


            public AKTimerTask() {
            }

            @Override
            public void run() {

            }
        }

    }

}
